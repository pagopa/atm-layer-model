package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public interface ResourceEntityService {
    Uni<ResourceEntity> save(ResourceEntity resourceEntity);

    Uni<Boolean> delete(BpmnVersionPK bpmnVersionPK);

    Uni<Optional<ResourceEntity>> findBySHA256(String sha256);

    Uni<Optional<ResourceEntity>> findByUUID(UUID uuid);

    Uni<ResourceEntity> saveAndUpload(ResourceEntity resourceEntity, File file, String filename, String path);

    Uni<ResourceEntity> createResource(ResourceEntity resourceEntity, File file, String filename,String path);

    Uni<ResourceEntity> updateResource (UUID uuid, ResourceEntity newResource,File file, String filename, String path);
}
