package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;

import java.io.File;
import java.net.URL;

public interface BpmnFileStorageService {
    Uni<ResourceFile> uploadFile(BpmnVersion bpmn, File file, String filename);
    Uni<URL> generatePresignedUrl(String objectKey);
}
