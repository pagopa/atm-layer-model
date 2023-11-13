package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStorePutResponse;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.strategy.ObjectStoreStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static it.gov.pagopa.atmlayer.service.model.utils.EnumConverter.convertEnum;

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
    public Uni<ResourceFile> uploadFile(ResourceEntity resourceEntity, File file, String filenameWithExtension, String relativePath) {
        S3ResourceTypeEnum resourceType = convertEnum(resourceEntity.getNoDeployableResourceType());
        String storageKey=calculateStorageKey(resourceEntity.getNoDeployableResourceType(),relativePath,resourceEntity.getFileName());
        String finalPath = calculateCompletePath(resourceEntity.getNoDeployableResourceType(),relativePath);
                    log.info("Requesting to write file {} in Object Store at path {}", file.getName(), finalPath);
                    Context context = Vertx.currentContext();
                    return objectStoreService.uploadFile(file, finalPath, resourceType, filenameWithExtension)
                            .emitOn(command -> context.runOnContext(x -> command.run()))
                            .onItem()
                            .transformToUni(objectStorePutResponse -> this.writeResourceInfoToDatabase(resourceEntity,
                                    objectStorePutResponse, filenameWithExtension.split("\\.")[0]));

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
                .resourceType(convertEnum(resourceEntity.getNoDeployableResourceType()))
                .resourceEntity(resourceEntity)
                .storageKey(putObjectResponse.getStorage_key())
                .build();
        return resourceFileService.save(entity);
    }

    @Override
    public String calculateBasePath(S3ResourceTypeEnum s3ResourceTypeEnum) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("RESOURCE_TYPE", s3ResourceTypeEnum.toString());
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

    @Override
    public String calculateCompletePath(NoDeployableResourceType resourceType, String relativePath){
        S3ResourceTypeEnum s3resourceType = convertEnum(resourceType);
        String path = calculateBasePath(s3resourceType);
        if (!relativePath.isBlank()) {
            path = path.concat("/").concat(relativePath);
        }
        return path;
    }

    @Override
    public String calculateStorageKey(NoDeployableResourceType resourceType, String relativePath,String fileName){
        return calculateCompletePath(resourceType,relativePath).concat("/").concat(fileName);
    }
}
