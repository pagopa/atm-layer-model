package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;

@ApplicationScoped
@Slf4j
public class ResourceEntityServiceImpl implements ResourceEntityService {

    @Inject
    ResourceEntityRepository resourceEntityRepository;

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
    public Uni<ResourceEntity> saveAndUpload(ResourceEntity resourceEntity, File file, String filename) {
        return this.save(resourceEntity);
        //TODO: implementare upload su FileStorageService
    }

    @Override
    public Uni<ResourceEntity> createResource(ResourceEntity resourceEntity, File file, String filename) {
        return findBySHA256(resourceEntity.getSha256())
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException("A resource with the same content already exists", Response.Status.BAD_REQUEST, BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS);
                    }
                    return saveAndUpload(resourceEntity, file, filename)
                            .onItem().transformToUni(bpmn -> {
                                return this.findByUUID(resourceEntity.getResourceId())
                                        .onItem().transformToUni(optionalResource -> {
                                            if (optionalResource.isEmpty()) {
                                                return Uni.createFrom().failure(new AtmLayerException("Sync problem on resource creation", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
                                            }
                                            return Uni.createFrom().item(optionalResource.get());
                                        });
                            });
                }));
    }
}
