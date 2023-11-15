package it.gov.pagopa.atmlayer.service.model.integrationtests;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import java.util.List;

@ApplicationScoped
@Slf4j
public class BucketCreationUtils {

    @Inject
    ObjectStoreProperties objectStoreProperties;

    @Inject
    S3AsyncClient s3AsyncClient;


    public void createBucketIfNotExisting() {
        ListBucketsResponse listBucketsResponse = Uni.createFrom().future(s3AsyncClient.listBuckets())
                .await().indefinitely();
        List<String> buckets = listBucketsResponse.buckets().stream().map(Bucket::name).toList();
        boolean alreadyExist = buckets.contains(objectStoreProperties.bucket().name());
        if (alreadyExist) {
            log.info("Bucket {} already exists", objectStoreProperties.bucket().name());
        }

        if (!alreadyExist) {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(objectStoreProperties.bucket().name())
                    .build();
            Uni.createFrom().future(s3AsyncClient.createBucket(createBucketRequest)).await().indefinitely();
            log.info("Bucket {} created", objectStoreProperties.bucket().name());
        }
    }
}
