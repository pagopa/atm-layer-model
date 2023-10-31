package it.gov.pagopa.atmlayer.service.model.resource.filestorage;

import it.gov.pagopa.atmlayer.service.model.model.filestorage.FormData;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

abstract public class FileStorageCommonResource {

    @Inject
    ObjectStoreProperties objectStoreProperties;

    protected PutObjectRequest buildPutRequest(FormData formData) {
        return PutObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(formData.filename)
                .contentType(formData.mimetype)
                .build();
    }

    protected GetObjectRequest buildGetRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(objectKey)
                .build();
    }

}