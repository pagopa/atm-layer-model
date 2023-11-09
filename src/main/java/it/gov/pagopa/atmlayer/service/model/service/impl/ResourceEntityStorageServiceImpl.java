package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStorePutResponse;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.strategy.ObjectStoreStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class ResourceEntityStorageServiceImpl implements ResourceEntityStorageService {
    @Inject
    ObjectStoreStrategy objectStoreStrategy;
    private ObjectStoreService objectStoreService;
    @Inject
    ObjectStoreProperties objectStoreProperties;
    @Inject
    ResourceFileService resourceFileService;

    public ResourceEntityStorageServiceImpl(ObjectStoreStrategy objectStoreStrategy,
                                            ObjectStoreProperties objectStoreProperties) {
        this.objectStoreStrategy = objectStoreStrategy;
        this.objectStoreService = objectStoreStrategy.getType(
                ObjectStoreStrategyEnum.fromValue(objectStoreProperties.type()));
    }

    @Override
    public Uni<ResourceFile> uploadFile(ResourceEntity resourceEntity, File file, String filename, String specificPath) {
        ResourceTypeEnum resourceType = resourceEntity.getResourceTypeEnum();
        String path = calculatePath(resourceType);
        if (!specificPath.isBlank()) {
            path = path.concat("/").concat(specificPath);
        }
        String completeName = filename.concat(".").concat(resourceType.getExtension());
        log.info("Requesting to write file {} in Object Store at path {}", file.getName(), path);
        Context context = Vertx.currentContext();
        return objectStoreService.uploadFile(file, path, resourceType, completeName)
                .emitOn(command -> context.runOnContext(x -> command.run()))
                .onItem()
                .transformToUni(objectStorePutResponse -> this.writeResourceInfoToDatabase(resourceEntity,
                        objectStorePutResponse, filename));
    }

    @Override
    public Uni<URL> generatePresignedUrl(String storageKey) {
        return this.objectStoreService.generatePresignedUrl(storageKey);
    }

    @Override
    public RestMulti<Buffer> download(String storageKey) {
        return this.objectStoreService.download(storageKey);
    }

    @WithTransaction
    public Uni<ResourceFile> writeResourceInfoToDatabase(ResourceEntity resourceEntity,
                                                         ObjectStorePutResponse putObjectResponse, String filename) {
        ResourceFile entity = ResourceFile.builder()
                .fileName(filename)
                .resourceType(resourceEntity.getResourceTypeEnum())
                .resourceEntity(resourceEntity)
                .storageKey(putObjectResponse.getStorage_key())
                .build();
        return resourceFileService.save(entity);
    }

    private String calculatePath(ResourceTypeEnum resourceTypeEnum) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("RESOURCE_TYPE", resourceTypeEnum.toString());
        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        Optional<String> resourceEntityPathTemplateProps = Optional.ofNullable(
                objectStoreProperties.html().pathTemplate());
        String pathTemplate = null;
        if (resourceEntityPathTemplateProps.isPresent() && StringUtils.isNotBlank(
                resourceEntityPathTemplateProps.get())) {
            pathTemplate = resourceEntityPathTemplateProps.get();
            pathTemplate = pathTemplate.replace("[", "${").replace("]", "}");
        }
        return stringSubstitutor.replace(pathTemplate);
    }
}
