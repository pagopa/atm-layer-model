package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.model.BpmnIdDto;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.net.URL;

public interface BpmnFileStorageService {
    Uni<PutObjectResponse> uploadFile(BpmnIdDto bpmnVersionPK, File file, String filename);
    Uni<URL> generatePresignedUrl(String objectKey);
}
