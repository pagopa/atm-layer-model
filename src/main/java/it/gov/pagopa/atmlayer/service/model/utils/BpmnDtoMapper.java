package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class BpmnDtoMapper {

    public static BpmnVersion toBpmnVersion(BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType(bpmnCreationDto.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(calculateSha256(bpmnCreationDto.getFile()));
        bpmnVersion.setDeployedFileName(bpmnCreationDto.getFilename().concat(".").concat(ResourceTypeEnum.BPMN.getExtension()));
        bpmnVersion.setEnabled(true);
        return bpmnVersion;
    }

    public static String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        byte[] array = BpmnUtils.toSha256ByteArray(file);
        return BpmnUtils.toHexString(array);
    }
}
