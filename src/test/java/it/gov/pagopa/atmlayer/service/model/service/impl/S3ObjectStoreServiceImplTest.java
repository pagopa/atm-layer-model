package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;


@QuarkusTest
class S3ObjectStoreServiceImplTest {

    @InjectMocks
    S3ObjectStoreServiceImpl s3ObjectStoreService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile_invalidFilename() {
        File file = new File("validFile.txt");
        String path = "validPath";
        S3ResourceTypeEnum fileType = S3ResourceTypeEnum.BPMN;
        String invalidFilename = "";

        assertThrows(AtmLayerException.class, () -> s3ObjectStoreService.uploadFile(file, path, fileType, invalidFilename));
    }

    @Test
    void testUploadFile_invalidPath() {
        File file = new File("validFile.txt");
        String invalidPath = "";
        S3ResourceTypeEnum fileType = S3ResourceTypeEnum.BPMN;
        String filename = "filename";

        assertThrows(AtmLayerException.class, () -> s3ObjectStoreService.uploadFile(file, invalidPath, fileType, filename));
    }

    @Test
    void testUploadFile_invalidFile() {
        File invalidFile = null;
        String path = "validPath";
        S3ResourceTypeEnum fileType = S3ResourceTypeEnum.BPMN;
        String filename = "filename";

        assertThrows(AtmLayerException.class, () -> s3ObjectStoreService.uploadFile(invalidFile, path, fileType, filename));
    }

}
