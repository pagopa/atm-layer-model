package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.utils.FileStorageS3Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

import static org.mockito.Mockito.when;


@QuarkusTest
public class S3ObjectStoreServiceImplTest {

    @Mock
    S3AsyncClient s3;

    @Mock
    FileStorageS3Utils fileStorageS3Utils;

    @Mock
    S3Presigner presigner;

    @InjectMocks
    S3ObjectStoreServiceImpl s3ObjectStoreService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


//    @Test
//    void testGeneratePresignedEmptyUrl() {
//        String objectKey = "";
//        String expectedURL="";
//
//        GetObjectRequest getObjectRequest = fileStorageS3Utils.buildGetRequest(objectKey);
//        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
//                .getObjectRequest(getObjectRequest)
//                .build();
//
//
//        when(presigner.presignGetObject(getObjectPresignRequest)).thenReturn(PresignedRequest.url(expectedURL).build());
//
//        Uni<URL> result= s3ObjectStoreService.generatePresignedUrl(objectKey);
//
//        result.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted().assertItem(expectedURL);
//    }


}
