package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceFileRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.DATABASE_SAVE_FILE_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.RESOURCE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.utils.FileStorageS3Utils.modifyPath;
import static java.lang.String.valueOf;

@ApplicationScoped
public class ResourceFileServiceImpl implements ResourceFileService {
    private final ResourceFileRepository resourceFileRepository;
    private final ObjectStoreProperties objectStoreProperties;

    @Inject
    public ResourceFileServiceImpl(ResourceFileRepository resourceFileRepository, ObjectStoreProperties objectStoreProperties){
        this.resourceFileRepository = resourceFileRepository;
        this.objectStoreProperties = objectStoreProperties;
    }

    @Override
    @WithTransaction
    public Uni<ResourceFile> save(ResourceFile resourceFile) {
        return this.resourceFileRepository.persist(resourceFile);
    }

    @Override
    @WithSession
    public Uni<Optional<ResourceFile>> findByStorageKey(String storageKey) {
        return resourceFileRepository.findByStorageKey(storageKey)
                .onItem().transformToUni(resource -> Uni.createFrom().item(Optional.ofNullable(resource)));
    }

    @Override
    @WithSession
    public Uni<String> getStorageKey(ResourceEntity resourceEntity) {
        return resourceFileRepository.findByResourceId(resourceEntity.getResourceId())
                .onItem().transformToUni(resourceFile -> Uni.createFrom().item(Optional.ofNullable(resourceFile)))
                .onItem().transformToUni(Unchecked.function(optionalResourceFile -> {
                    if (optionalResourceFile.isEmpty()) {
                        throw new AtmLayerException("La risorsa di riferimento non esiste: impossibile recuperare la chiave di archiviazione", Response.Status.BAD_REQUEST, RESOURCE_DOES_NOT_EXIST);
                    }
                    return Uni.createFrom().item(optionalResourceFile.get().getStorageKey());
                }));
    }

    @Override
    @WithTransaction
    public Uni<ResourceFile> updateStorageKey(ResourceEntity resourceEntity) {
        return resourceFileRepository.findByStorageKey(resourceEntity.getResourceFile().getStorageKey())
                .onItem().transformToUni(resourceFileFound -> {
                    if (resourceFileFound == null) {
                        throw new AtmLayerException("La risorsa di riferimento non esiste: impossibile aggiornare la chiave di archiviazione", Response.Status.BAD_REQUEST, RESOURCE_DOES_NOT_EXIST);
                    }
                    String newStorageKey = modifyPath(resourceFileFound.getStorageKey());
                    resourceFileFound.setStorageKey(newStorageKey);
                    return resourceFileRepository.persist(resourceFileFound)
                            .onItem()
                            .transformToUni(newResourceFile -> Uni.createFrom().item(newResourceFile));
                })
                .onFailure().recoverWithUni(failure ->
                    Uni.createFrom().failure(new AtmLayerException(
                            "Impossibile eliminare la risorsa. Aggiornamento della storage key interrotto",
                            Response.Status.INTERNAL_SERVER_ERROR, DATABASE_SAVE_FILE_ERROR))
                );
    }

    @Override
    @WithSession
    public Uni<String> getCompletePath(ResourceEntity resourceEntity) {
        return getStorageKey(resourceEntity)
                .onItem().transform(completePath -> completePath.substring(0, completePath.lastIndexOf("/")));
    }

    @Override
    @WithSession
    public Uni<String> getRelativePath(ResourceEntity resourceEntity) {
        String resourceType = valueOf(resourceEntity.getNoDeployableResourceType());
        String basePath = objectStoreProperties.resource().pathTemplate();
        String basePathWithoutType = basePath.substring(0, basePath.lastIndexOf("/"));
        return getCompletePath(resourceEntity)
                .onItem().transform(completeBasePath -> StringUtils.stripEnd(completeBasePath.replace(basePathWithoutType, ""), "/"))
                .onItem().transform(relativePathWithType -> relativePathWithType.substring(relativePathWithType.indexOf("/") + 1)
                        .replace(resourceType, ""))
                .onItem().transform(relativePathWithSlash -> relativePathWithSlash.substring(relativePathWithSlash.indexOf("/") + 1));
    }
}
