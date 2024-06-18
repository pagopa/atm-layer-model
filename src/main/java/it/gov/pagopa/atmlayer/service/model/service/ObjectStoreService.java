package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStoreResponse;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;

public interface ObjectStoreService {


    ObjectStoreStrategyEnum getType();

    Uni<ObjectStoreResponse> uploadFile(File file, String path, S3ResourceTypeEnum fileType, String filename);

    Uni<ObjectStoreResponse> uploadDisabledFile(String storageKey, S3ResourceTypeEnum fileType, String filename);

    Uni<URL> generatePresignedUrl(String objectKey);

    RestMulti<Buffer> download(String key);
}
