package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;

public interface S3ObjectStoreService extends ObjectStoreService {
    Uni<PutObjectResponse> uploadFile(File file, String path, ResourceTypeEnum fileType, String filename);

}
