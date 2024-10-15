package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.UtilityValues;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import it.gov.pagopa.atmlayer.service.model.utils.CommonUtils;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
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
        return resourceEntityRepository.persist(resourceEntity)
                .onFailure().recoverWithUni(dbException -> {
                    log.info(dbException.getMessage());
                    return Uni.createFrom().failure(new AtmLayerException(
                            String.format("Errore nel salvataggio della risorsa. %s", dbException.getMessage()),
                            Response.Status.INTERNAL_SERVER_ERROR, DATABASE_SAVE_FILE_ERROR));
                });
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
        validateFileExtension(filename, Arrays.asList("html", "jpeg", "jpg", "png", "svg"));

        return findBySHA256(resourceEntity.getSha256())
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException(String.format("Esiste già una risorsa con lo stesso contenuto: %s",
                                x.get().getResourceFile().getStorageKey().substring(15)),
                                Response.Status.BAD_REQUEST, RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS);
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


    private void validateFileExtension(String filename, List<String> validExtensions) {
        String fileExtension = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
                .orElse("");

        if (!validExtensions.contains(fileExtension)) {
            throw new AtmLayerException(String.format("Estensione del file non valida: %s. Estensioni consentite: %s",
                    fileExtension, String.join(", ", validExtensions)),
                    Response.Status.BAD_REQUEST, AppErrorCodeEnum.INVALID_FILE_EXTENSION);
        }
    }


    @Override
    @WithTransaction
    public Uni<List<String>> createResourceMultiple(List<ResourceEntity> resourceEntityList, List<ResourceCreationDto> resourceCreationDtoList) {
        List<String> errors = new ArrayList<>();
        List<String> uploadedFiles = new ArrayList<>();

        long totalFileSize = resourceCreationDtoList.stream()
                .mapToLong(dto -> dto.getFile().length())  // ottieni la dimensione del file in byte
                .sum();

        // Se la somma delle dimensioni dei file supera 10MB, solleva un'eccezione
        if (totalFileSize > 10 * 1024 * 1024) {
            throw new AtmLayerException("La dimensione totale dei file supera il limite di 10MB", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500);
        }

        return Multi.createFrom().items(resourceEntityList.stream())
                .onItem().transformToUniAndConcatenate(resourceEntity -> {
                    int index = resourceEntityList.indexOf(resourceEntity);
                    File file = resourceCreationDtoList.get(index).getFile();
                    String filename = resourceCreationDtoList.get(index).getFilename();

                    try {
                        validateFileExtension(filename, Arrays.asList("html", "jpeg", "jpg", "png", "svg"));
                    } catch (AtmLayerException ex) {
                        errors.add(String.format("%s-%s", filename, ex.getMessage()));
                        return Uni.createFrom().nullItem();
                    }

                    return findBySHA256(resourceEntity.getSha256())
                            .onItem().transformToUni(x -> {
                                if (x.isPresent()) {
                                    errors.add(String.format("%s-Esiste già una risorsa con lo stesso contenuto", filename));
                                    return Uni.createFrom().nullItem();
                                }
                                return resourceFileService.findByStorageKey(resourceEntity.getStorageKey())
                                        .onItem()
                                        .transformToUni(resource -> {
                                            if (resource.isPresent()) {
                                                errors.add(String.format("%s-Impossibile caricare: la risorsa con lo stesso nome file e percorso esiste già", filename));
                                                return Uni.createFrom().nullItem();
                                            }
                                            return saveAndUpload(resourceEntity, file, filename, resourceCreationDtoList.get(index).getPath())
                                                    .onItem().transformToUni(bpmn -> this.findByUUID(resourceEntity.getResourceId())
                                                            .onItem().transformToUni(optionalResource -> {
                                                                if (optionalResource.isEmpty()) {
                                                                    errors.add(String.format("%s-Problema di sincronizzazione sulla creazione della risorsa", filename));
                                                                    return Uni.createFrom().nullItem();
                                                                }
                                                                uploadedFiles.add(optionalResource.get().getStorageKey());
                                                                return Uni.createFrom().item(optionalResource.get());
                                                            }));
                                        });
                            })
                            .onFailure().recoverWithItem(throwable -> {
                                log.error("Error processing file {}: {}", filename, throwable.getMessage());
                                errors.add(String.format("%s: %s", filename, throwable.getMessage()));
                                return null;
                            });
                })
                .collect().asList()
                .onItem().transformToUni(resourceDTOList -> {
                    if (!errors.isEmpty()) {
                        return deleteResourcesFromStorage(uploadedFiles, errors);
                    }
                    FileUtilities.cleanDecodedFilesDirectory();
                    return Uni.createFrom().item(errors);  // This will be empty if no errors occurred
                });
    }

    public Uni<List<String>> deleteResourcesFromStorage(List<String> storageKeys, List<String> errorMessages) {
        return Multi.createFrom().items(storageKeys.stream())
                .onItem().transformToUniAndConcatenate(uploadedStorageKey -> resourceEntityStorageService.delete(uploadedStorageKey)
                        .onItem().transform(objectStoreResponse -> String.format("Deleted %s", objectStoreResponse.getStorageKey())))
                .collect().asList()
                .onItem().transform(Unchecked.function(deletedKeys -> {
                    FileUtilities.cleanDecodedFilesDirectory();
                    throw new AtmLayerException("Errore nel caricamento dovuto ai seguenti file: " + String.join(", ", errorMessages),
                            Response.Status.BAD_REQUEST, RESOURCES_CREATION_ERROR);
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
                    return findBySHA256(newFileSha256)
                            .onItem().transformToUni(Unchecked.function(x -> {
                                if (x.isPresent()) {
                                    throw new AtmLayerException(String.format("Esiste già una risorsa con lo stesso contenuto: %s", x.get().getResourceFile().getStorageKey().substring(15)),
                                            Response.Status.BAD_REQUEST,
                                            RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS);
                                }
                                resourceEntity.setSha256(newFileSha256);
                                Date date = new Date();
                                resourceEntity.setLastUpdatedAt(new Timestamp(date.getTime()));
                                resourceEntity.getResourceFile().setLastUpdatedAt(new Timestamp(date.getTime()));
                                return resourceEntityRepository.persist(resourceEntity)
                                        .onItem()
                                        .transformToUni(savedResource -> {
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
    @WithTransaction
    public Uni<Void> disable(UUID uuid) {
        String errorMessage = "Errore durante la cancellazione della risorsa: Controllare i log per i dettagli";
        return resourceEntityRepository.findById(uuid)
                .onItem()
                .transformToUni(resourceEntity -> {
                    String originalStorageKey = resourceEntity.getResourceFile().getStorageKey();
                    String originalFileName = resourceEntity.getResourceFile().getFileName();
                    S3ResourceTypeEnum originalType = resourceEntity.getResourceFile().getResourceType();
                    return this.setDisabledResourceEntityAttributes(uuid)
                            .onItem()
                            .transformToUni(disabledEntity -> resourceFileService.updateStorageKey(disabledEntity)
                                    .onItem()
                                    .transformToUni(resourceFileUpdated -> resourceEntityStorageService.uploadDisabledFile(originalStorageKey, resourceFileUpdated.getStorageKey(), originalType, originalFileName)
                                            .onItem()
                                            .transformToUni(itemToDelete -> resourceEntityStorageService.delete(originalStorageKey)
                                                    .onItem()
                                                    .transformToUni(deletedFile -> Uni.createFrom().voidItem())
                                                    .onFailure()
                                                    .recoverWithUni(deleteException -> resourceEntityStorageService.delete(resourceFileUpdated.getStorageKey())
                                                            .onItem()
                                                            .transform(Unchecked.function(deletedCopy -> {
                                                                throw new AtmLayerException(
                                                                        errorMessage,
                                                                        Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_COPY_FILE_ERROR);})))
                                            )
                                    )
                                    .onFailure().recoverWithUni(failure ->
                                            Uni.createFrom().failure(new AtmLayerException(
                                                    errorMessage,
                                                    Response.Status.INTERNAL_SERVER_ERROR, DATABASE_SAVE_FILE_ERROR))
                                    )
                            );
                });
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
