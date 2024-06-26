package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.UtilityValues;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import it.gov.pagopa.atmlayer.service.model.utils.CommonUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.*;
import static it.gov.pagopa.atmlayer.service.model.utils.FileUtilities.calculateSha256;

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

    @Override
    @WithSession
    public Uni<List<ResourceEntity>> getAll() {
        return this.resourceEntityRepository.findAll().list();
    }

    @Override
    @WithTransaction
    public Uni<ResourceEntity> save(ResourceEntity resourceEntity) {
        return resourceEntityRepository.persist(resourceEntity);
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

    public Uni<ResourceEntity> checkResourceEntityExistence(UUID uuid) {
        return this.findByUUID(uuid)
                .onItem()
                .transform(Unchecked.function(optionalResource -> {
                    if (optionalResource.isEmpty()) {
                        String errorMessage = String.format("La risorsa con Id %s non esiste", uuid);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, RESOURCE_DOES_NOT_EXIST);
                    }
                    return optionalResource.get();
                }));
    }

    @Override
    @WithTransaction
    public Uni<ResourceEntity> saveAndUpload(ResourceEntity resourceEntity, File file,
                                             String filename, String path) {
        return this.save(resourceEntity)
                .onItem().transformToUni(element -> upload(resourceEntity, file, filename, path)
                        .onItem().transformToUni(putObjectResponse -> {
                            log.info("Completed Resource Entity Creation");
                            return Uni.createFrom().item(element);
                        }));
    }

    @Override
    public Uni<ResourceFile> upload(ResourceEntity resourceEntity, File file,
                                    String filename, String path) {
        return this.resourceEntityStorageService.saveFile(resourceEntity, file, filename, path)
                .onFailure().recoverWithUni(failure -> {
                    log.error(failure.getMessage());
                    return Uni.createFrom().failure(new AtmLayerException(
                            "Impossibile salvare l'entità risorsa nell'Object Store. Creazione della risorsa interrotta",
                            Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                });
    }

    @Override
    public Uni<ResourceEntity> createResource(ResourceEntity resourceEntity, File file,
                                              String filename, String path, String description) {
        return findBySHA256(resourceEntity.getSha256())
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException(String.format("Esiste già una risorsa con lo stesso contenuto: %s", x.get().getResourceFile().getStorageKey().substring(15)),
                                Response.Status.BAD_REQUEST,
                                RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS);
                    }
                    return resourceFileService.findByStorageKey(resourceEntity.getStorageKey())
                            .onItem()
                            .transformToUni(Unchecked.function(resource -> {
                                if (resource.isPresent()) {
                                    throw new AtmLayerException(String.format("Impossibile caricare %s: la risorsa con lo stesso nome file e percorso esiste già",
                                            resourceEntity.getStorageKey()), Response.Status.BAD_REQUEST, AppErrorCodeEnum.RESOURCE_WITH_SAME_NAME_AND_PATH_ALREADY_SAVED);
                                }
                                return saveAndUpload(resourceEntity, file, filename, path)
                                        .onItem().transformToUni(bpmn -> this.findByUUID(resourceEntity.getResourceId())
                                                .onItem().transformToUni(optionalResource -> {
                                                    if (optionalResource.isEmpty()) {
                                                        return Uni.createFrom().failure(
                                                                new AtmLayerException("Problema di sincronizzazione sulla creazione della risorsa",
                                                                        Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
                                                    }
                                                    return Uni.createFrom().item(optionalResource.get());
                                                }));
                            }));
                }));
    }

    @Override
    @WithTransaction
    public Uni<ResourceEntity> updateResource(UUID uuid, File file) {
        return this.findByUUID(uuid)
                .onItem()
                .transformToUni(Unchecked.function(optionalResource -> {
                    if (optionalResource.isEmpty()) {
                        String errorMessage = String.format("La risorsa con Id %s non esiste: impossibile aggiornarla", uuid);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, RESOURCE_DOES_NOT_EXIST);
                    }
                    ResourceEntity resourceEntity = optionalResource.get();
                    String newFileSha256 = calculateSha256(file);
                    if (Objects.equals(resourceEntity.getSha256(), newFileSha256)) {
                        throw new AtmLayerException("La risorsa è già presente", Response.Status.BAD_REQUEST, RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS);
                    }
                    resourceEntity.setSha256(newFileSha256);
                    Date date = new Date();
                    resourceEntity.setLastUpdatedAt(new Timestamp(date.getTime()));
                    resourceEntity.getResourceFile().setLastUpdatedAt(new Timestamp(date.getTime()));
                    return resourceEntityRepository.persist(resourceEntity)
                            .onItem()
                            .transformToUni(x -> {
                                String storageKey = resourceEntity.getResourceFile().getStorageKey();
                                String fileName = FilenameUtils.getBaseName(storageKey);
                                String extension = FilenameUtils.getExtension(storageKey);
                                String path = FilenameUtils.getFullPath(storageKey);
                                return resourceEntityStorageService.uploadFile(file, resourceEntity, CommonUtils.getFilenameWithExtension(fileName, extension), CommonUtils.getPathWithoutFilename(path), false)
                                        .onItem()
                                        .transformToUni(fileUpdated -> this.findByUUID(uuid)
                                                .onItem().transformToUni(optionalResourceUpdated -> Uni.createFrom().item(optionalResource.get())));
                            });
                }));
    }

    @Override
    @WithSession
    public Uni<PageInfo<ResourceEntity>> findResourceFiltered(int pageIndex, int pageSize, UUID resourceId, String sha256, NoDeployableResourceType noDeployableResourceType, String fileName, String storageKey, String extension) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("resourceId", resourceId);
        filters.put("sha256", sha256);
        if (noDeployableResourceType != null) filters.put("noDeployableResourceType", noDeployableResourceType.name());
        filters.put("fileName", fileName);
        filters.put("storageKey", storageKey);
        filters.put("extension", extension);
        filters.remove(null);
        filters.values().removeAll(Collections.singleton(null));
        filters.values().removeAll(Collections.singleton(""));
        return resourceEntityRepository.findByFilters(filters, pageIndex, pageSize);
    }

    @Override
    public Uni<Void> disable(UUID uuid) {
        return this.setDisabledResourceEntityAttributes(uuid)
                .onItem()
                .transformToUni(disabledResourceEntity -> Uni.createFrom().voidItem());
    }

    @WithTransaction
    public Uni<ResourceEntity> setDisabledResourceEntityAttributes(UUID uuid) {
        return this.checkResourceEntityExistence(uuid)
                .onItem().transformToUni(resourceEntity -> {
                    resourceEntity.setEnabled(false);
                    String disabledSha = resourceEntity.getSha256().concat(UtilityValues.DISABLED_FLAG.getValue()).concat(resourceEntity.getResourceId().toString());
                    resourceEntity.setSha256(disabledSha);
                    return this.resourceEntityRepository.persist(resourceEntity);
                });
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteResource(UUID uuid) {
        return resourceEntityRepository.deleteById(uuid)
                .onItem().transformToUni(deleted ->
                        Boolean.FALSE.equals(deleted) ? Uni.createFrom().failure(new AtmLayerException(String.format("Impossibile eliminare la risorsa con Id %s: non esiste oppure si è verificato un errore durante la cancellazione", uuid),
                                Response.Status.BAD_REQUEST, RESOURCE_DOES_NOT_EXIST)) :
                                Uni.createFrom().voidItem());
    }
}
