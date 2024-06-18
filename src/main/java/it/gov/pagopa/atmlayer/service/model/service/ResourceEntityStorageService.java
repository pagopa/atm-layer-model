package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;

public interface ResourceEntityStorageService {

  Uni<ResourceFile> uploadFile(File file, ResourceEntity resourceEntity, String filename, String path, boolean creation);

  Uni<ResourceFile> uploadDisabledFile(ResourceEntity resourceEntity);

  Uni<URL> generatePresignedUrl(String storageKey);

  RestMulti<Buffer> download(String storageKey);

  String calculateBasePath(S3ResourceTypeEnum s3ResourceTypeEnum);

  String calculateCompletePath(NoDeployableResourceType resourceType, String relativePath);

  String calculateStorageKey(NoDeployableResourceType resourceType, String relativePath, String fileName);

  Uni<ResourceFile> saveFile(ResourceEntity resourceEntity, File file, String fileNameWithExtension, String relativePath);

}
