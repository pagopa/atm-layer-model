package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.configurations.S3PreSignerLocal;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


@QuarkusTest
public class S3ObjectStoreServiceImplTest {

    @Mock
    ObjectStoreProperties objectStoreProperties;

    @InjectMocks
    S3ObjectStoreServiceImpl s3ObjectStoreService;

    S3PreSignerLocal s3PreSignerLocal;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        s3PreSignerLocal = new S3PreSignerLocal();
        s3PreSignerLocal.objectStoreProperties = objectStoreProperties;
    }


    @Test
    public void testUploadFile_invalidFilename() {
        List<BpmnBankConfig> mockConfigs = Collections.singletonList(new BpmnBankConfig());

        File file = new File("validFile.txt");
        String path = "validPath";
        S3ResourceTypeEnum fileType = S3ResourceTypeEnum.BPMN;
        String invalidFilename = "";

        assertThrows(AtmLayerException.class, () -> s3ObjectStoreService.uploadFile(file, path, fileType, invalidFilename));

    }

    @Test
    public void testUploadFile_invalidPath() {
        List<BpmnBankConfig> mockConfigs = Collections.singletonList(new BpmnBankConfig());

        File file = new File("validFile.txt");
        String invalidPath = "";
        S3ResourceTypeEnum fileType = S3ResourceTypeEnum.BPMN;
        String filename = "filename";

        assertThrows(AtmLayerException.class, () -> s3ObjectStoreService.uploadFile(file, invalidPath, fileType, filename));

    }

    @Test
    public void testUploadFile_invalidFile() {
        List<BpmnBankConfig> mockConfigs = Collections.singletonList(new BpmnBankConfig());

        File invalidFile = null;
        String path = "validPath";
        S3ResourceTypeEnum fileType = S3ResourceTypeEnum.BPMN;
        String filename = "filename";

        assertThrows(AtmLayerException.class, () -> s3ObjectStoreService.uploadFile(invalidFile, path, fileType, filename));

    }

//    @Test
//    public void testUploadFileWithError() {
//        File file = new File("validFile.txt");
//        String path = "validPath";
//        S3ResourceTypeEnum fileType = S3ResourceTypeEnum.BPMN;
//        String filename = "validFilename";
//        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
//            @Override
//            public String name() {
//                return "test-bucket";
//            }
//
//            @Override
//            public Optional<String> endpointOverride() {
//                return Optional.of("http://localhost:9000");
//            }
//
//            @Override
//            public String region() {
//                return "us-east-1";
//            }
//
//            @Override
//            public Optional<String> secretKey() {
//                return Optional.of("your-secret-key");
//            }
//
//            @Override
//            public Optional<String> accessKey() {
//                return Optional.of("your-access-key");
//            }
//        });
//
//
//        S3AsyncClient s3AsyncClient = s3PreSignerLocal.s3AsyncClient();
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket("test-bucket")
//                .key("nome-del-tuo-oggetto")
//                .build();
//
//// Creare un oggetto AsyncRequestBody utilizzando il metodo fromFile
//        AsyncRequestBody mockRequestBody = mock(AsyncRequestBody.class);
//
//// Quando viene chiamato il metodo fromFile con qualsiasi argomento, restituire il mock
//        when(AsyncRequestBody.fromFile(any(File.class))).thenReturn(mockRequestBody);
//
//
//        when(s3AsyncClient.putObject(putObjectRequest,mockRequestBody))
//                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Simulated error")));
//
//        assertThrows(AtmLayerException.class, () -> {
//            s3ObjectStoreService.uploadFile(file, path, fileType, filename).await().indefinitely();
//        });
//    }
}
