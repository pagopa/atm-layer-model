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
    private ResourceEntityServiceImpl resourceEntityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
//    @Test
//    public void getAll(){
//        ResourceEntity resourceEntity=new ResourceEntity();
//        when(resourceEntityRepository.findAll()).thenReturn((PanacheQuery<ResourceEntity>)resourceEntity);
//        resourceEntityService.getAll()
//                .subscribe().withSubscriber(UniAssertSubscriber.create())
//                .assertCompleted();
//    }

    @Test
    void uploadFailure() {
        File file = new File("src/test/resources/Test.bpmn");
        when(resourceEntityStorageService.saveFile(any(ResourceEntity.class), any(File.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        resourceEntityService.upload(new ResourceEntity(), file, "filename", "path")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Failed to save Resource Entity in Object Store. Resource creation aborted");
    }

    @Test
    void testCreateAlreadyExists() {
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile = new ResourceFile();
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setStorageKey("storageKey");
        when(resourceEntityRepository.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(resourceFileService.findByStorageKey(any(String.class))).thenReturn(Uni.createFrom().item(Optional.of(resourceFile)));
        resourceEntityService.createResource(resourceEntity, file, "filename", "path")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Cannot upload storageKey: resource with same file name and path already exists");
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
        resourceEntityService.createResource(resourceEntity, file, "filename", "path")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Sync problem on resource creation");
    }
}