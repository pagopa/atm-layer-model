package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Mapper(componentModel = "cdi")
public abstract class BpmnVersionMapper {

    @Mapping(ignore = true, target = "file")
    @Mapping(ignore = true, target = "filename")
    public abstract BpmnCreationDto toDtoCreation(BpmnVersion bpmnVersion);

    public BpmnVersion toEntityCreation(BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType(bpmnCreationDto.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(BpmnUtils.calculateSha256(bpmnCreationDto.getFile()));
        bpmnVersion.setDeployedFileName(bpmnCreationDto.getFilename().concat(".").concat(ResourceTypeEnum.BPMN.getExtension()));
        bpmnVersion.setEnabled(true);
        return bpmnVersion;
    }

    public abstract BpmnDTO toDTO(BpmnVersion bpmnVersion);

    @Mapping(ignore = true, target = "enabled")
    @Mapping(target = "resourceFile.bpmn",ignore = true)
    public abstract BpmnVersion toEntity(BpmnDTO bpmnDTO);
}
