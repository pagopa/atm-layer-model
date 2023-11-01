package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStorePutResponse;

import java.io.File;
import java.net.URL;

public interface S3ObjectStoreService extends ObjectStoreService {
    Uni<ObjectStorePutResponse> uploadFile(File file, String path, ResourceTypeEnum fileType, String filename);

    Uni<URL> generatePresignedUrl(String objectKey);

}
