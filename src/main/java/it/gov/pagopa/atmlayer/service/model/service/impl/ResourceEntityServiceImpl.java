package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.INEXISTENT_RESOURCE_CANNOT_BE_UPDATED;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS;

@ApplicationScoped
@Slf4j
public class ResourceEntityServiceImpl implements ResourceEntityService {
    @Inject
    ResourceEntityStorageService resourceEntityStorageService;
    @Inject
    ResourceEntityRepository resourceEntityRepository;
    @Inject
    ResourceFileService resourceFileService;
    @Inject
    @RestClient
    ProcessClient processClient;
    final S3ResourceTypeEnum resourceType = S3ResourceTypeEnum.HTML;

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
                .onItem().transformToUni(record -> upload(resourceEntity, file, filename, path)
                        .onItem().transformToUni(putObjectResponse -> {
                            log.info("Completed Resource Entity Creation");
                            return Uni.createFrom().item(record);
                        }));
    }

    @Override
    public Uni<ResourceFile> upload(ResourceEntity resourceEntity, File file,
                                    String filename, String path) {
        return this.resourceEntityStorageService.uploadFile(resourceEntity, file, filename, path)
                .onFailure().recoverWithUni(failure -> {
                    log.error(failure.getMessage());
                    return Uni.createFrom().failure(new AtmLayerException(
                            "Failed to save Resource Entity in Object Store. Resource creation aborted",
                            Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                });
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
                    return resourceFileService.findByStorageKey(resourceEntity.getStorageKey())
                            .onItem()
                            .transformToUni(Unchecked.function(resource -> {
                                if (resource.isPresent()) {
                                    throw new AtmLayerException(String.format("Cannot upload %s: resource with same file name and path already exists",
                                            resourceEntity.getStorageKey()), Response.Status.BAD_REQUEST, AppErrorCodeEnum.RESOURCE_WITH_SAME_NAME_AND_PATH_ALREADY_SAVED);
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
                }));
    }

    @Override
    public Uni<ResourceFile> updateResource(UUID uuid, File file) {
        return this.findByUUID(uuid)
                .onItem().transformToUni(optionalResource -> {
                    if (optionalResource.isEmpty()) {
                        String errorMessage = String.format("Resource with Id %s does not exist: cannot update", uuid);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, INEXISTENT_RESOURCE_CANNOT_BE_UPDATED);
                    }
                    return Uni.createFrom().item(optionalResource.get());
                })
                .onItem().transformToUni(resource ->
                        resourceFileService.getRelativePath(resource)
                                .onItem().transformToUni(path -> upload(resource, file, resource.getFileName(), path)));
    }
}
