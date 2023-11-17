package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStorePutResponse;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public interface ObjectStoreService {


    ObjectStoreStrategyEnum getType();

    Uni<ObjectStorePutResponse> uploadFile(File file, String path, S3ResourceTypeEnum fileType, String filename) throws IOException;

    Uni<URL> generatePresignedUrl(String objectKey);

    RestMulti<Buffer> download(String key);
}
