package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ApplicationScoped
public class FileStorageS3Utils {
    @Inject
    ObjectStoreProperties objectStoreProperties;

    public PutObjectRequest buildPutRequest(String filename, String mimetype, String path) {
        return PutObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(path.concat("/").concat(filename))
                .contentType(mimetype)
                .build();
    }

    public GetObjectRequest buildGetRequest(String key) {
        return GetObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(key)
                .build();
    }

}
