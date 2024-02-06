package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourceEntityService {

    Uni<ResourceEntity> save(ResourceEntity resourceEntity);

    Uni<Optional<ResourceEntity>> findBySHA256(String sha256);

    Uni<Optional<ResourceEntity>> findByUUID(UUID uuid);

    Uni<ResourceEntity> saveAndUpload(ResourceEntity resourceEntity, File file, String filename,
                                      String path);

    Uni<ResourceFile> upload(ResourceEntity resourceEntity, File file, String filename, String path);

    Uni<ResourceEntity> createResource(ResourceEntity resourceEntity, File file, String filename, String path);

    Uni<ResourceEntity> updateResource(UUID uuid, File file);


    Uni<List<ResourceEntity>> getAll();

    Uni<PageInfo<ResourceEntity>> findResourceFiltered(int pageIndex, int pageSize, UUID resourceId, String sha256, NoDeployableResourceType noDeployableResourceType, String fileName, String storageKey, String extension);
}
