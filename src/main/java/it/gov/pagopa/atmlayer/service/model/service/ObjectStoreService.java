package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStorePutResponse;

import java.io.File;
import java.net.URL;

public interface ObjectStoreService {


    ObjectStoreStrategyEnum getType();

    Uni<ObjectStorePutResponse> uploadFile(File file, String path, ResourceTypeEnum fileType, String filename);

    Uni<URL> generatePresignedUrl(String objectKey);
}
