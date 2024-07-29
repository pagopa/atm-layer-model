package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStoreResponse;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class ResourceEntityServiceImplTest {
    @Mock
    ResourceEntityRepository resourceEntityRepository;
    @Mock
    ResourceEntityStorageServiceImpl resourceEntityStorageService;
    @Mock
    ResourceFileServiceImpl resourceFileService;
    @Mock
    ResourceEntity resourceEntity;
    @InjectMocks
    ResourceEntityServiceImpl resourceEntityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFailure() {
        File file = new File("path/to/file");
        when(resourceEntityStorageService.saveFile(any(ResourceEntity.class), any(File.class), any(String.class), any(String.class)))
                .thenReturn(Uni.createFrom().failure(new RuntimeException()));
        resourceEntityService.upload(new ResourceEntity(), file, "filename", "path")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Impossibile salvare l'entità risorsa nell'Object Store. Creazione della risorsa interrotta");
    }

    @Test
    void testCreateAlreadyExists() {
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setStorageKey("originalStorageKey");
        resourceFile.setFileName("originalFileName");
        resourceFile.setResourceType(S3ResourceTypeEnum.OTHER);
        File file = new File("path/to/file");

        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setStorageKey("storageKey");
        when(resourceEntityRepository.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(resourceFileService.findByStorageKey(any(String.class))).thenReturn(Uni.createFrom().item(Optional.of(resourceFile)));
        resourceEntityService.createResource(resourceEntity, file, "filename", "path", "description")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Impossibile caricare storageKey: la risorsa con lo stesso nome file e percorso esiste già");
    }

    @Test
    void testCreateSyncError() {
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setStorageKey("originalStorageKey");
        resourceFile.setFileName("originalFileName");
        resourceFile.setResourceType(S3ResourceTypeEnum.OTHER);

        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setResourceFile(resourceFile);
        resourceEntity.setSha256("originalSha256");

        when(resourceEntityRepository.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(resourceFileService.findByStorageKey(any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(resourceEntityRepository.persist(any(ResourceEntity.class))).thenReturn(Uni.createFrom().item(resourceEntity));
        when(resourceEntityStorageService.saveFile(any(ResourceEntity.class), any(File.class), any(String.class), any(String.class)))
                .thenReturn(Uni.createFrom().item(resourceFile));
        when(resourceEntityRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        resourceEntityService.createResource(resourceEntity, new File("path/to/file"), "filename.xml", "path", "description")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Problema di sincronizzazione sulla creazione della risorsa");
    }

    @Test
    void testCreateResourceWithDuplicateSHA256() {
        String sha256 = "duplicateSHA256";
        File file = new File("path/to/file");
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setStorageKey("storageKeyThatIsLongEnoughToAvoidException");
        ResourceEntity existingResourceEntity = new ResourceEntity();
        existingResourceEntity.setResourceFile(resourceFile);
        existingResourceEntity.setSha256(sha256);


        when(resourceEntityRepository.findBySHA256(sha256)).thenReturn(Uni.createFrom().item(existingResourceEntity));

        resourceEntityService.createResource(existingResourceEntity, file, "filename", "path", "description")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Esiste già una risorsa con lo stesso contenuto");
    }

    @Test
    void testCreateResourceSuccess() {
        UUID uuid = UUID.randomUUID();
        ResourceEntity resourceEntity1 = new ResourceEntity();
        resourceEntity1.setResourceId(uuid);
        String sha256 = "uniqueSHA256";
        resourceEntity1.setSha256(sha256);
        File file = new File("path/to/file");
        ResourceFile resourceFile = new ResourceFile();

        when(resourceEntityRepository.findBySHA256(sha256)).thenReturn(Uni.createFrom().nullItem());
        when(resourceFileService.findByStorageKey(any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(resourceEntityRepository.persist(any(ResourceEntity.class))).thenReturn(Uni.createFrom().item(resourceEntity1));
        when(resourceEntityStorageService.saveFile(resourceEntity1, file, "prova.txt", "/file/prova")).thenReturn(Uni.createFrom().item(resourceFile));
        when(resourceEntityRepository.findById(uuid)).thenReturn(Uni.createFrom().item(resourceEntity1));

        resourceEntityService.createResource(resourceEntity1, file, "filename", "path", "description")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(resourceEntity1);
    }


    @Test
    void testDisableSuccess() {
        UUID uuid = UUID.randomUUID();
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setStorageKey("originalStorageKey");
        resourceFile.setFileName("originalFileName");
        resourceFile.setResourceType(S3ResourceTypeEnum.OTHER);

        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setResourceId(uuid);
        resourceEntity.setResourceFile(resourceFile);
        resourceEntity.setSha256("originalSha256");

        when(resourceEntityRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(resourceEntity));
        when(resourceEntityRepository.persist(any(ResourceEntity.class))).thenReturn(Uni.createFrom().item(resourceEntity));
        when(resourceFileService.updateStorageKey(any(ResourceEntity.class))).thenReturn(Uni.createFrom().item(resourceFile));
        when(resourceEntityStorageService.uploadDisabledFile(anyString(), anyString(), any(S3ResourceTypeEnum.class), anyString()))
                .thenReturn(Uni.createFrom().item(new ObjectStoreResponse("storageKey")));
        when(resourceEntityStorageService.delete(anyString())).thenReturn(Uni.createFrom().item(new ObjectStoreResponse("storageKey")));

        resourceEntityService.disable(uuid)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }

    @Test
    void testCheckResourceEntityExistence_ResourceDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(resourceEntityService.findByUUID(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());

        resourceEntityService.checkResourceEntityExistence(uuid)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, String.format("La risorsa con Id %s non esiste", uuid));
    }

    @Test
    void testUpdateResourceNotFound() {
        UUID uuid = UUID.randomUUID();
        File file = new File("newFile.txt");

        when(resourceEntityService.findByUUID(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());

        resourceEntityService.updateResource(uuid, file)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, String.format("La risorsa con Id %s non esiste: impossibile aggiornarla", uuid));
    }

    /*@Test
    public void testUpdateResource_ResourceAlreadyExists(@TempDir Path tempDir) throws NoSuchAlgorithmException, IOException {
        UUID uuid = UUID.randomUUID();
        Path tempFilePath = Files.createFile(tempDir.resolve("testFile"));
        File file = tempFilePath.toFile();
        ResourceEntity existingResource = new ResourceEntity();
        existingResource.setSha256("existingSha256");
        Optional<ResourceEntity> optionalResource = Optional.of(existingResource);

        when(resourceEntityService.findByUUID(uuid)).thenReturn(Uni.createFrom().item(optionalResource));

        AtmLayerException exception = Assertions.assertThrows(AtmLayerException.class, () -> {
            resourceEntityService.updateResource(uuid, file).await().indefinitely();
        });
        Assertions.assertEquals("La risorsa è già presente", exception.getMessage());
    }

    @Test
    public void testUpdateResource_ResourceUpdatedSuccessfully(@TempDir Path tempDir) throws IOException {
        UUID uuid = UUID.randomUUID();
        Path tempFilePath = Files.createFile(tempDir.resolve("testFile"));
        File file = tempFilePath.toFile();

        ResourceEntity existingResource = new ResourceEntity();
        existingResource.setSha256("oldSha256");
        existingResource.setFileName("oldFileName.txt");
        existingResource.setResourceFile(new ResourceFile());
        existingResource.getResourceFile().setStorageKey("path/to/oldFile.txt");
        Optional<ResourceEntity> optionalResource = Optional.of(existingResource);

        when(resourceEntityService.findByUUID(uuid)).thenReturn(Uni.createFrom().item(optionalResource));

        String newSha256 = "newSha256";

        ResourceEntity updatedResource = new ResourceEntity();
        updatedResource.setSha256(newSha256);
        updatedResource.setFileName(existingResource.getFileName());
        updatedResource.setResourceFile(existingResource.getResourceFile());

        when(resourceEntityRepository.persist(any(ResourceEntity.class))).thenAnswer(invocation -> {
            ResourceEntity entityToPersist = invocation.getArgument(0);
            entityToPersist.setSha256(newSha256); // Simula l'aggiornamento effettivo nel repository
            return Uni.createFrom().item(entityToPersist);
        });

        when(resourceEntityStorageService.uploadFile(any(File.class), any(ResourceEntity.class), anyString(), anyString(), anyBoolean()))
                .thenReturn(Uni.createFrom().item(existingResource.getResourceFile()));

        resourceEntityService.updateResource(uuid, file)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(updatedResource);
    }*/

    @Test
    public void testFindResourceFiltered() {
        UUID resourceId = UUID.randomUUID();
        String sha256 = "someSha256";
        NoDeployableResourceType noDeployableResourceType = NoDeployableResourceType.OTHER;
        String fileName = "example.txt";
        String storageKey = "someKey";
        String extension = "txt";

        PageInfo<ResourceEntity> expectedPageInfo = new PageInfo<>(0, 10, 1, 1, Collections.emptyList());

        when(resourceEntityRepository.findByFilters(any(), anyInt(), anyInt())).thenReturn(Uni.createFrom().item(expectedPageInfo));

        Uni<PageInfo<ResourceEntity>> result = resourceEntityService.findResourceFiltered(0, 10, resourceId, sha256, noDeployableResourceType, fileName, storageKey, extension);

        assertNotNull(result);
        PageInfo<ResourceEntity> resultPageInfo = result.await().indefinitely();
        assertNotNull(resultPageInfo);
        assertEquals(expectedPageInfo.getResults().size(), resultPageInfo.getResults().size());
        assertEquals(expectedPageInfo.getItemsFound(), resultPageInfo.getItemsFound());

        Map<String, Object> expectedFilters = new HashMap<>();
        expectedFilters.put("resourceId", resourceId);
        expectedFilters.put("sha256", sha256);
        expectedFilters.put("noDeployableResourceType", noDeployableResourceType.name());
        expectedFilters.put("fileName", fileName);
        expectedFilters.put("storageKey", storageKey);
        expectedFilters.put("extension", extension);
        Mockito.verify(resourceEntityRepository).findByFilters(expectedFilters, 0, 10);
    }

    @Test
    public void testDeleteResourceSuccess() {
        UUID uuid = UUID.randomUUID();

        when(resourceEntityRepository.deleteById(uuid)).thenReturn(Uni.createFrom().item(true));

        resourceEntityService.deleteResource(uuid)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }

    @Test
    public void testDeleteResourceFailure() {
        UUID uuid = UUID.randomUUID();

        when(resourceEntityRepository.deleteById(uuid)).thenReturn(Uni.createFrom().item(false));

        resourceEntityService.deleteResource(uuid)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, String.format("Impossibile eliminare la risorsa con Id %s: non esiste oppure si è verificato un errore durante la cancellazione", uuid));
    }

    @Test
    public void testGetAll() {
        UUID uuid = UUID.randomUUID();
        ResourceEntity resourceEntity1 = new ResourceEntity();
        resourceEntity1.setResourceId(uuid);
        List<ResourceEntity> list = new ArrayList<>();
        list.add(resourceEntity1);

        // Mock della PanacheQuery anziché del risultato diretto
        PanacheQuery<ResourceEntity> mockQuery = mock(PanacheQuery.class);
        when(resourceEntityRepository.findAll()).thenReturn(mockQuery);
        when(mockQuery.list()).thenReturn(Uni.createFrom().item(list));

        resourceEntityService.getAll()
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }

}
