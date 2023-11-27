package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class ResourceFileServiceImplTest {
    @Mock
    ResourceFileRepository resourceFileRepository;
    @Mock
    ObjectStoreProperties objectStoreProperties;
    @Mock
    ObjectStoreProperties.Resource objectStorePropertiesResource;
    @InjectMocks
    ResourceFileServiceImpl resourceFileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveResourceFile() {
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setFileName("test.txt");
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        when(resourceFileRepository.persist(resourceFile)).thenReturn(Uni.createFrom().item(resourceFile));
        Uni<ResourceFile> savedResourceFile = resourceFileService.save(resourceFile);
        ResourceFile result = savedResourceFile.await().indefinitely();
        assertEquals(resourceFile, result);
    }

    @Test
    void testGetMethodsOK() {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setResourceId(UUID.randomUUID());
        ResourceFile expectedResourceFile = new ResourceFile();
        expectedResourceFile.setStorageKey("base/path/test/relative/type/example.file");
        when(resourceFileRepository.findByResourceId(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedResourceFile));
        resourceFileService.getStorageKey(resourceEntity)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem("base/path/test/relative/type/example.file");
        resourceFileService.getCompletePath(resourceEntity)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem("base/path/test/relative/type");
        when(objectStoreProperties.resource()).thenReturn(objectStorePropertiesResource);
        when(objectStorePropertiesResource.pathTemplate()).thenReturn("base/path/test");
        resourceFileService.getRelativePath(resourceEntity)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem("relative/type");
    }

    @Test
    void testGetStorageKeyResourceDoesNotExist() {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setResourceId(UUID.randomUUID());
        when(resourceFileRepository.findByResourceId(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        resourceFileService.getStorageKey(resourceEntity)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "The referenced resource does not exist: cannot retrieve storage key");
    }
}










