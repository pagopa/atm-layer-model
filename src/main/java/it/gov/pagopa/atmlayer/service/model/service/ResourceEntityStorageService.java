package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import org.jboss.resteasy.reactive.RestMulti;
import java.io.File;
import java.net.URL;

public interface ResourceEntityStorageService {

  Uni<ResourceFile> uploadFile(ResourceEntity resourceEntity, File file, String filename, String path);

  Uni<URL> generatePresignedUrl(String storageKey);

  RestMulti<Buffer> download(String storageKey);

}
