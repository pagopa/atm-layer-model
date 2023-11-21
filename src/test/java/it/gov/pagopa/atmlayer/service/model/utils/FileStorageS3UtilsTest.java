package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class FileStorageS3UtilsTest {

    @InjectMocks
    private FileStorageS3Utils fileStorageS3Utils;

    @Mock
    private ObjectStoreProperties objectStoreProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuildPutRequest() {
        when(objectStoreProperties.type()).thenReturn("type");
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            public String name() {
                return "testBucket";
            }

            public Optional<String> endpointOverride() {
                return Optional.of("http://endpoint");
            }

            public String region() {
                return "eu-south-1";
            }

            public Optional<String> secretKey() {
                return Optional.of("secretKey");
            }

            public Optional<String> accessKey() {
                return Optional.of("accessKey");
            }
        });

        PutObjectRequest putObjectRequest = fileStorageS3Utils.buildPutRequest("testFile.txt", "text/plain", "testPath");

        assertEquals("testBucket", putObjectRequest.bucket());
        assertEquals("testPath/testFile.txt", putObjectRequest.key());
        assertEquals("text/plain", putObjectRequest.contentType());


        Map<String, String> expectedMetadata = new HashMap<>();
        expectedMetadata.put("Content-Type", "text/plain");
        assertEquals(expectedMetadata, putObjectRequest.metadata());
    }

    @Test
    void testBuildGetRequest() {
        when(objectStoreProperties.type()).thenReturn("type");
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {

            public String name() {
                return "testBucket";
            }

            public Optional<String> endpointOverride() {
                return Optional.of("http://endpoint");
            }

            public String region() {
                return "eu-south-1";
            }

            public Optional<String> secretKey() {
                return Optional.of("secretKey");
            }

            public Optional<String> accessKey() {
                return Optional.of("accessKey");
            }
        });

        GetObjectRequest getObjectRequest = fileStorageS3Utils.buildGetRequest("testPath/testFile.txt");

        assertEquals("testBucket", getObjectRequest.bucket());
        assertEquals("testPath/testFile.txt", getObjectRequest.key());
    }
}


