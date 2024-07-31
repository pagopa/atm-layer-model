package it.gov.pagopa.atmlayer.service.model.configurations;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class S3PreSignerLocalTest {

    @Mock
    ObjectStoreProperties objectStoreProperties;

    S3PreSignerLocal s3PreSignerLocal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        s3PreSignerLocal = new S3PreSignerLocal(objectStoreProperties);
    }

    @Test
    void testLocalPreSigner() {
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            @Override
            public String name() {
                return "test-bucket";
            }

            @Override
            public Optional<String> endpointOverride() {
                return Optional.of("http://localhost:9000");
            }

            @Override
            public String region() {
                return "us-east-1";
            }

            @Override
            public Optional<String> secretKey() {
                return Optional.of("your-secret-key");
            }

            @Override
            public Optional<String> accessKey() {
                return Optional.of("your-access-key");
            }
        });

        S3Presigner s3Presigner = s3PreSignerLocal.localPreSigner();

        assertNotNull(s3Presigner);
    }

    @Test
    void testS3AsyncClientWithMissingEndpoint() {
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            @Override
            public String name() {
                return "test-bucket";
            }

            @Override
            public Optional<String> endpointOverride() {
                return Optional.empty();
            }

            @Override
            public String region() {
                return "us-east-1";
            }

            @Override
            public Optional<String> secretKey() {
                return Optional.of("your-secret-key");
            }

            @Override
            public Optional<String> accessKey() {
                return Optional.of("your-access-key");
            }
        });

        assertThrows(RuntimeException.class, () -> s3PreSignerLocal.s3AsyncClient());
    }

    @Test
    void testLocalPreSignerWithMissingEndpoint() {
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            @Override
            public String name() {
                return "test-bucket";
            }

            @Override
            public Optional<String> endpointOverride() {
                return Optional.empty();
            }

            @Override
            public String region() {
                return "us-east-1";
            }

            @Override
            public Optional<String> secretKey() {
                return Optional.of("your-secret-key");
            }

            @Override
            public Optional<String> accessKey() {
                return Optional.of("your-access-key");
            }
        });

        assertThrows(RuntimeException.class, () -> s3PreSignerLocal.localPreSigner());
    }

    @Test
    void testS3AsyncClient() {
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            @Override
            public String name() {
                return "test-bucket";
            }

            @Override
            public Optional<String> endpointOverride() {
                return Optional.of("http://localhost:9000");
            }

            @Override
            public String region() {
                return "us-east-1";
            }

            @Override
            public Optional<String> secretKey() {
                return Optional.of("your-secret-key");
            }

            @Override
            public Optional<String> accessKey() {
                return Optional.of("your-access-key");
            }
        });

        S3AsyncClient s3AsyncClient = s3PreSignerLocal.s3AsyncClient();

        assertNotNull(s3AsyncClient);
        assertEquals("s3", s3AsyncClient.serviceName());
    }

    @Test
    void testS3AsyncClientWithMissingSecretKey() {
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            @Override
            public String name() {
                return "test-bucket";
            }

            @Override
            public Optional<String> endpointOverride() {
                return Optional.of("http://localhost:9000");
            }

            @Override
            public String region() {
                return "us-east-1";
            }

            @Override
            public Optional<String> secretKey() {
                return Optional.empty();
            }

            @Override
            public Optional<String> accessKey() {
                return Optional.of("your-access-key");
            }
        });

        assertThrows(AtmLayerException.class, () -> s3PreSignerLocal.s3AsyncClient());
    }

    @Test
    void testS3AsyncClientWithMissingAccessKey() {
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            @Override
            public String name() {
                return "test-bucket";
            }

            @Override
            public Optional<String> endpointOverride() {
                return Optional.of("http://localhost:9000");
            }

            @Override
            public String region() {
                return "us-east-1";
            }

            @Override
            public Optional<String> secretKey() {
                return Optional.of("your-secret-key");
            }

            @Override
            public Optional<String> accessKey() {
                return Optional.empty();
            }
        });

        assertThrows(AtmLayerException.class, () -> s3PreSignerLocal.s3AsyncClient());
    }

    @Test
    void testS3AsyncClientWithMissingAccessKeyAndSecretKey() {
        when(objectStoreProperties.bucket()).thenReturn(new ObjectStoreProperties.Bucket() {
            @Override
            public String name() {
                return "test-bucket";
            }

            @Override
            public Optional<String> endpointOverride() {
                return Optional.of("http://localhost:9000");
            }

            @Override
            public String region() {
                return "us-east-1";
            }

            @Override
            public Optional<String> secretKey() {
                return Optional.empty();
            }

            @Override
            public Optional<String> accessKey() {
                return Optional.empty();
            }
        });

        assertThrows(AtmLayerException.class, () -> s3PreSignerLocal.s3AsyncClient());
    }
}
