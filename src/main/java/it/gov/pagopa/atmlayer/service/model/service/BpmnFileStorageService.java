package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;

public interface BpmnFileStorageService {
    Uni<ResourceFile> uploadFile(BpmnVersion bpmn, File file, String filename);

    Uni<URL> generatePresignedUrl(String storageKey);

    RestMulti<Buffer> download(String storageKey);
}
