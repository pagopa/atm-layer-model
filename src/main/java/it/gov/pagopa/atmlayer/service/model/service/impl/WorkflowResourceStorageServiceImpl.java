package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStoreResponse;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceStorageService;
import it.gov.pagopa.atmlayer.service.model.strategy.ObjectStoreStrategy;
import it.gov.pagopa.atmlayer.service.model.utils.CommonUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.utils.EnumConverter.convertEnum;

@ApplicationScoped
@Slf4j
public class WorkflowResourceStorageServiceImpl implements WorkflowResourceStorageService {

    private static final String WORKFLOW_TEMPLATE_PATH_DEFAULT = "WORKFLOW_RESOURCE/files/${RESOURCE_TYPE}/${uuid}";

    private final ObjectStoreStrategy objectStoreStrategy;

    private final ObjectStoreService objectStoreService;

    private final ObjectStoreProperties objectStoreProperties;

    private final ResourceFileService resourceFileService;

    @Inject
    public WorkflowResourceStorageServiceImpl(ObjectStoreStrategy objectStoreStrategy, ObjectStoreProperties objectStoreProperties,
                                              ResourceFileService resourceFileService) {
        this.objectStoreStrategy = objectStoreStrategy;
        this.objectStoreService = objectStoreStrategy.getType(ObjectStoreStrategyEnum.fromValue(objectStoreProperties.type()));
        this.objectStoreProperties = objectStoreProperties;
        this.resourceFileService = resourceFileService;
    }

    @Override
    public Uni<ResourceFile> uploadFile(WorkflowResource workflowResource, File file, String filename) {
        UUID uuid = workflowResource.getWorkflowResourceId();
        S3ResourceTypeEnum resourceType = convertEnum(workflowResource.getResourceType());
        String path = calculatePath(uuid, resourceType);
        return doUploadFile(workflowResource, file, path, filename, resourceType.getExtension(), resourceType);

    }

    @Override
    @WithSession
    public Uni<ResourceFile> updateFile(WorkflowResource workflowResource, File file) {
        String storageKey = workflowResource.getResourceFile().getStorageKey();
        S3ResourceTypeEnum resourceType = convertEnum(workflowResource.getResourceType());
        String path = FilenameUtils.getFullPath(storageKey);
        Context context = Vertx.currentContext();
        log.info("Requesting to update file in Object Store");
        return objectStoreService.uploadFile(file, CommonUtils.getPathWithoutFilename(path), resourceType, CommonUtils.getFilenameWithExtensionFromStorageKey(storageKey))
                .emitOn(command -> context.runOnContext(x -> command.run()))
                .onItem().transformToUni(x -> this.resourceFileService.findByStorageKey(storageKey))
                .onItem().transformToUni(Unchecked.function(y -> {
                    if (y.isEmpty()) {
                        throw new AtmLayerException("Non viene fatto riferimento alla chiave di archiviazione", Response.Status.NOT_FOUND, AppErrorCodeEnum.RESOURCE_FILE_DOES_NOT_EXIST);
                    }
                    return Uni.createFrom().item(y.get());
                }));
    }

    private Uni<ResourceFile> doUploadFile(WorkflowResource workflowResource, File file, String path, String filename, String extension, S3ResourceTypeEnum resourceType) {
        Context context = Vertx.currentContext();
        log.info("Requesting to write file in Object Store");
        return objectStoreService.uploadFile(file, path, resourceType, CommonUtils.getFilenameWithExtension(filename, extension))
                .emitOn(command -> context.runOnContext(x -> command.run()))
                .onItem()
                .transformToUni(objectStorePutResponse -> this.writeResourceInfoToDatabase(workflowResource, objectStorePutResponse, filename));

    }

    @WithTransaction
    public Uni<ResourceFile> writeResourceInfoToDatabase(WorkflowResource workflowResource, ObjectStoreResponse putObjectResponse, String filename) {
        ResourceFile entity = ResourceFile.builder()
                .fileName(filename)
                .resourceType(convertEnum(workflowResource.getResourceType()))
                .workflowResource(workflowResource)
                .storageKey(putObjectResponse.getStorageKey())
                .build();
        return resourceFileService.save(entity);
    }

    @Override
    public Uni<URL> generatePresignedUrl(String storageKey) {
        return this.objectStoreService.generatePresignedUrl(storageKey);
    }

    @Override
    public RestMulti<Buffer> download(String storageKey) {
        return this.objectStoreService.download(storageKey);
    }

    private String calculatePath(UUID uuid, S3ResourceTypeEnum resourceType) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("uuid", uuid.toString());
        valuesMap.put("RESOURCE_TYPE", resourceType.toString());
        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        Optional<String> workflowResourcePathTemplateProps = Optional.ofNullable(objectStoreProperties.workflowResource().pathTemplate());
        String pathTemplate = WORKFLOW_TEMPLATE_PATH_DEFAULT;
        if (workflowResourcePathTemplateProps.isPresent() && StringUtils.isNotBlank(workflowResourcePathTemplateProps.get())) {
            pathTemplate = workflowResourcePathTemplateProps.get();
            pathTemplate = pathTemplate.replace("[", "${").replace("]", "}");
        }
        return stringSubstitutor.replace(pathTemplate);
    }
}
