package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;

public interface WorkflowResourceStorageService {

    Uni<ResourceFile> uploadFile(WorkflowResource workflowResource, File file, String filename);

    Uni<ResourceFile> updateFile(WorkflowResource workflowResource, File file);

    Uni<URL> generatePresignedUrl(String storageKey);

    RestMulti<Buffer> download(String storageKey);
}
