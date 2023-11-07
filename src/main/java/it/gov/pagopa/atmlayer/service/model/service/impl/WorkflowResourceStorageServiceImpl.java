package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnIdDto;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStorePutResponse;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceStorageService;
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
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class WorkflowResourceStorageServiceImpl implements WorkflowResourceStorageService {

    private final String WORKFLOW_TEMPLATE_PATH_DEFAULT = "WORKFLOW_RESOURCE/files/${RESOURCE_TYPE}/${uuid}";

    @Inject
    ObjectStoreStrategy objectStoreStrategy;

    private ObjectStoreService objectStoreService;

    @Inject
    ObjectStoreProperties objectStoreProperties;

    @Inject
    ResourceFileService resourceFileService;

    public WorkflowResourceStorageServiceImpl(ObjectStoreStrategy objectStoreStrategy, ObjectStoreProperties objectStoreProperties) {
        this.objectStoreStrategy = objectStoreStrategy;
        this.objectStoreService = objectStoreStrategy.getType(ObjectStoreStrategyEnum.fromValue(objectStoreProperties.type()));
    }

    @Override
    public Uni<ResourceFile> uploadFile(WorkflowResource workflowResource, File file, String filename) {
        UUID uuid = workflowResource.getWorkflowResourceId();
        String path = calculatePath(uuid);
        String completeName = filename.concat(".").concat(ResourceTypeEnum.DMN.getExtension());
        log.info("Requesting to write file {} in Object Store at path  {}", file.getName(), path);
        Context context = Vertx.currentContext();
        return objectStoreService.uploadFile(file, path, ResourceTypeEnum.DMN, completeName)
                .emitOn(command -> context.runOnContext(x -> command.run()))
                .onItem()
                .transformToUni(objectStorePutResponse -> this.writeResourceInfoToDatabase(workflowResource, objectStorePutResponse, filename));

    }

    @WithTransaction
    public Uni<ResourceFile> writeResourceInfoToDatabase(WorkflowResource workflowResource, ObjectStorePutResponse putObjectResponse, String filename) {
        ResourceFile entity = ResourceFile.builder()
                .fileName(filename)
                .resourceType(ResourceTypeEnum.DMN)
                .workflowResource(workflowResource)
                .storageKey(putObjectResponse.getStorage_key())
                .build();
        return resourceFileService.save(entity);
    }

    @Override
    public Uni<URL> generatePresignedUrl(String storageKey) {
        return null;
    }

    @Override
    public RestMulti<Buffer> download(String storageKey) {
        return null;
    }

    //TODO: fix {$RESOURCE_TYPE}
    private String calculatePath(UUID uuid) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("uuid", uuid.toString());
        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        Optional<String> workflowResourcePathTemplateProps = Optional.ofNullable(objectStoreProperties.dmn().pathTemplate());
        String pathTemplate = WORKFLOW_TEMPLATE_PATH_DEFAULT;
        if (workflowResourcePathTemplateProps.isPresent() && StringUtils.isNotBlank(workflowResourcePathTemplateProps.get())) {
            pathTemplate = workflowResourcePathTemplateProps.get();
            pathTemplate = pathTemplate.replace("[", "${").replace("]", "}");
        }
        return stringSubstitutor.replace(pathTemplate);
    }
}
