package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ResourceFileServiceImplTest {

    @Mock
    ResourceFileRepository resourceFileRepository;

    @InjectMocks
    ResourceFileServiceImpl resourceFileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveResourceFile() {
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setFileName("test.txt");
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);

        when(resourceFileRepository.persist(resourceFile)).thenReturn(Uni.createFrom().item(resourceFile));

        Uni<ResourceFile> savedResourceFile = resourceFileService.save(resourceFile);

        ResourceFile result = savedResourceFile.await().indefinitely();
        assertEquals(resourceFile, result);
    }
}