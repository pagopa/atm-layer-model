package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.model.CreationMetadata;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class BpmnDtoConverter {

    public static BpmnVersion converter(BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK();
        CreationMetadata creationMetadata = bpmnCreationDto.getCreationMetadata();
        bpmnVersionPK.setBpmnId(creationMetadata.getBpmnId());
        bpmnVersionPK.setModelVersion(creationMetadata.getModelVersion());
        bpmnVersion.setBpmnVersionPK(bpmnVersionPK);
        bpmnVersion.setDeployedFileName(creationMetadata.getDeployedFileName());
        bpmnVersion.setDefinitionKey(creationMetadata.getDefinitionKey());
        bpmnVersion.setFunctionType(creationMetadata.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(calculateSha256(bpmnCreationDto.getFile()));
        //TODO: capisci come fare
        bpmnVersion.setEnabled(true);
        return bpmnVersion;
    }

    public static String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        //TODO: Controllare che il file sia un xml .bpmn
        byte[] array = ModelUtils.toSha256ByteArray(file);
        return ModelUtils.toHexString(array);
    }
}
