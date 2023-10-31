package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.net.URL;

public interface ObjectStoreService {


    ObjectStoreStrategyEnum getType();

    Uni<PutObjectResponse> uploadFile(File file, String path, ResourceTypeEnum fileType, String filename);

    Uni<URL> generatePresignedUrl(String objectKey);
}
