package it.gov.pagopa.atmlayer.service.model.service.impl;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Slf4j
public class ResourceEntityServiceImpl implements ResourceEntityService {

  @Inject
  ResourceEntityStorageService resourceEntityStorageService;
  @Inject
  ResourceEntityRepository resourceEntityRepository;

  @Inject
  @RestClient
  ProcessClient processClient;

  final S3ResourceTypeEnum resourceType = S3ResourceTypeEnum.HTML;

  @Override
  @WithSession
  public Uni<List<ResourceEntity>> getAll() {
    return this.resourceEntityRepository.findAll().list();
  }

  @Override
  @WithTransaction
  public Uni<ResourceEntity> save(ResourceEntity resourceEntity) {
    log.info("Persisting resource {} to database", resourceEntity.getFileName());
    return resourceEntityRepository.persist(resourceEntity);
  }

  @Override
  public Uni<Boolean> delete(BpmnVersionPK bpmnVersionPK) {
    return null;
  }

  @Override
  @WithSession
  public Uni<Optional<ResourceEntity>> findBySHA256(String sha256) {
    return this.resourceEntityRepository.findBySHA256(sha256)
        .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
  }

  @Override
  @WithSession
  public Uni<Optional<ResourceEntity>> findByUUID(UUID uuid) {
    return resourceEntityRepository.findById(uuid)
        .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
  }

  @Override
  @WithTransaction
  public Uni<ResourceEntity> saveAndUpload(ResourceEntity resourceEntity, File file,
      String filename, String path) {
    return this.save(resourceEntity)
        .onItem().transformToUni(
            record -> this.resourceEntityStorageService.uploadFile(resourceEntity, file, filename,
                    path)
                .onFailure().recoverWithUni(failure -> {
                  log.error(failure.getMessage());
                  return Uni.createFrom().failure(new AtmLayerException(
                      "Failed to save Resource Entity in Object Store. Resource creation aborted",
                      Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                })
                .onItem().transformToUni(putObjectResponse -> {
                  log.info("Completed Resource Entity Creation");
                  return Uni.createFrom().item(record);
                }));
  }

  @Override
  public Uni<ResourceEntity> createResource(ResourceEntity resourceEntity, File file,
      String filename, String path) {
    return findBySHA256(resourceEntity.getSha256())
        .onItem().transformToUni(Unchecked.function(x -> {
          if (x.isPresent()) {
            throw new AtmLayerException("A resource with the same content already exists",
                Response.Status.BAD_REQUEST,
                RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS);
          }
          return saveAndUpload(resourceEntity, file, filename, path)
              .onItem().transformToUni(bpmn -> this.findByUUID(resourceEntity.getResourceId())
                  .onItem().transformToUni(optionalResource -> {
                    if (optionalResource.isEmpty()) {
                      return Uni.createFrom().failure(
                          new AtmLayerException("Sync problem on resource creation",
                              Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
                    }
                    return Uni.createFrom().item(optionalResource.get());
                  }));
        }));
  }
}
