package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class ResourceEntityServiceImplTest {
    @Mock
    ResourceEntityRepository resourceEntityRepository;
    @Mock
    ResourceEntityStorageServiceImpl resourceEntityStorageService;
    @Mock
    ResourceFileServiceImpl resourceFileService;
    @InjectMocks
    ResourceEntityServiceImpl resourceEntityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void uploadFailure() {
        File file = new File("src/test/resources/Test.bpmn");
        when(resourceEntityStorageService.saveFile(any(ResourceEntity.class), any(File.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        resourceEntityService.upload(new ResourceEntity(), file, "filename", "path")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Impossibile salvare l'entità risorsa nell'Object Store. Creazione della risorsa interrotta");
    }

    @Test
    void testCreateAlreadyExists() {
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile = new ResourceFile();
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
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile = new ResourceFile();
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setStorageKey("storageKey");
        when(resourceEntityRepository.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(resourceFileService.findByStorageKey(any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(resourceEntityRepository.persist(any(ResourceEntity.class))).thenReturn(Uni.createFrom().item(resourceEntity));
        when(resourceEntityStorageService.saveFile(any(ResourceEntity.class), any(File.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        when(resourceEntityRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        resourceEntityService.createResource(resourceEntity, file, "filename.xml", "path", "description")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Problema di sincronizzazione sulla creazione della risorsa");
    }
}