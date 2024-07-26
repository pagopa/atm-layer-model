package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.dto.FileS3Dto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnIdDto;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStoreResponse;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import it.gov.pagopa.atmlayer.service.model.strategy.ObjectStoreStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class BpmnFileStorageServiceImpl implements BpmnFileStorageService {
    private static final String BPMN_TEMPLATE_PATH_DEFAULT = "BPMN/files/UUID/${uuid}/VERSION/${version}";
    private final ObjectStoreStrategy objectStoreStrategy;
    private final ObjectStoreService objectStoreService;
    private final ObjectStoreProperties objectStoreProperties;
    private final ResourceFileService resourceFileService;

    @Inject
    public BpmnFileStorageServiceImpl(ObjectStoreStrategy objectStoreStrategy, ObjectStoreProperties objectStoreProperties,
                                      ResourceFileService resourceFileService) {
        this.objectStoreStrategy = objectStoreStrategy;
        this.objectStoreService = objectStoreStrategy.getType(ObjectStoreStrategyEnum.fromValue(objectStoreProperties.type()));
        this.objectStoreProperties = objectStoreProperties;
        this.resourceFileService = resourceFileService;
    }

    @WithTransaction
    public Uni<ResourceFile> writeResourceInfoToDatabase(BpmnVersion bpmn, ObjectStoreResponse putObjectResponse, String filename) {
        ResourceFile entity = ResourceFile.builder()
                .fileName(filename)
                .resourceType(S3ResourceTypeEnum.BPMN)
                .bpmn(bpmn)
                .storageKey(putObjectResponse.getStorageKey())
                .build();
        return resourceFileService.save(entity);
    }

    @Override
    public Uni<ResourceFile> uploadFile(BpmnVersion bpmnVersion, File file, String filename) {
        BpmnIdDto bpmnVersionPK = new BpmnIdDto(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion());
        String path = calculatePath(bpmnVersionPK);
        String completeName = filename.concat(".").concat(S3ResourceTypeEnum.BPMN.getExtension());
        log.info("Requesting to upload file in Object Store");
        Context context = Vertx.currentContext();
        return objectStoreService.uploadFile(file, path, S3ResourceTypeEnum.BPMN, completeName)
                .emitOn(command -> context.runOnContext(x -> command.run()))
                .onItem()
                .transformToUni(objectStorePutResponse -> this.writeResourceInfoToDatabase(bpmnVersion, objectStorePutResponse, filename));
    }

    @Override
    public Uni<URL> generatePresignedUrl(String objectKey) {
        return this.objectStoreService.generatePresignedUrl(objectKey);
    }

    public RestMulti<Buffer> download(String storageKey) {
        return this.objectStoreService.download(storageKey);
    }

    public Uni<FileS3Dto> downloadForFrontEnd(String storageKey) {
        Context context = Vertx.currentContext();
        return this.objectStoreService.download(storageKey)
                .collect().asList()
                .onItem().transform(buffers -> {
                    Buffer totalBuffer = Buffer.buffer();
                    for (Buffer buffer : buffers) {
                        totalBuffer.appendBuffer(buffer);
                    }
                    String encoded = Base64.getEncoder().encodeToString(totalBuffer.getBytes());
                    return new FileS3Dto(encoded);
                })
                .emitOn(command -> context.runOnContext(x -> command.run()));
    }

    private String calculatePath(BpmnIdDto bpmnVersionPK) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("uuid", bpmnVersionPK.getBpmnId().toString());
        valuesMap.put("version", bpmnVersionPK.getModelVersion().toString());
        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        Optional<String> bpmnPathTemplateProps = Optional.ofNullable(objectStoreProperties.bpmn().pathTemplate());
        String pathTemplate = BPMN_TEMPLATE_PATH_DEFAULT;
        if (bpmnPathTemplateProps.isPresent() && StringUtils.isNotBlank(bpmnPathTemplateProps.get())) {
            pathTemplate = bpmnPathTemplateProps.get();
            pathTemplate = pathTemplate.replace("[", "${").replace("]", "}");
        }
        return stringSubstitutor.replace(pathTemplate);
    }
}
