package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class FileStorageS3Utils {
    @Inject
    ObjectStoreProperties objectStoreProperties;

    public PutObjectRequest buildPutRequest(String filename, String mimetype, String path) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", mimetype);
        return PutObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(path.concat("/").concat(filename))
                //.contentType(mimetype)
                //.metadata(metadata)
                .build();
    }

    public GetObjectRequest buildGetRequest(String key) {
        return GetObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(key)
                .build();
    }

}
