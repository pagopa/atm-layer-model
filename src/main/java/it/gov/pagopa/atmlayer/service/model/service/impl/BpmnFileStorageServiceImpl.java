package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnIdDto;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.strategy.ObjectStoreStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class BpmnFileStorageServiceImpl implements BpmnFileStorageService {

    private final String BPMN_TEMPLATE_PATH_DEFAULT = "/BPMN/files/UUID/${uuid}/VERSION/${version}";

    @Inject
    ObjectStoreStrategy objectStoreStrategy;

    private ObjectStoreService objectStoreService;

    @Inject
    ObjectStoreProperties objectStoreProperties;

    public BpmnFileStorageServiceImpl(ObjectStoreStrategy objectStoreStrategy, ObjectStoreProperties objectStoreProperties) {
        this.objectStoreStrategy = objectStoreStrategy;
        this.objectStoreService = objectStoreStrategy.getType(ObjectStoreStrategyEnum.fromValue(objectStoreProperties.type()));
    }

    @Override
    public Uni<PutObjectResponse> uploadFile(BpmnIdDto bpmnVersionPK, File file, String filename) {
        String path = calculatePath(bpmnVersionPK);
        String completeName = filename.concat(".").concat(ResourceTypeEnum.BPMN.getExtension());
        log.info("Requesting to write file {} in Object Store at path  {}", file.getName(), path);
        return objectStoreService.uploadFile(file, path, ResourceTypeEnum.BPMN, completeName);

    }

    @Override
    public Uni<URL> generatePresignedUrl(String objectKey) {
        return this.objectStoreService.generatePresignedUrl(objectKey);
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
