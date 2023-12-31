package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnProcessDTO;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class BpmnVersionMapper {

    @Mapping(ignore = true, target = "file")
    @Mapping(ignore = true, target = "filename")
    public abstract BpmnCreationDto toDtoCreation(BpmnVersion bpmnVersion);

    public BpmnVersion toEntityCreation(BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType(bpmnCreationDto.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(FileUtilities.calculateSha256(bpmnCreationDto.getFile()));
        bpmnVersion.setDeployedFileName(bpmnCreationDto.getFilename().concat(".").concat(S3ResourceTypeEnum.BPMN.getExtension()));
        bpmnVersion.setEnabled(true);
        return bpmnVersion;
    }

    public abstract BpmnDTO toDTO(BpmnVersion bpmnVersion);

    public abstract BpmnProcessDTO toProcessDTO(BpmnDTO bpmnProcessDTO);

    public List<BpmnDTO> toDTOList(List<BpmnVersion> list) {
        return list.stream().map(this::toDTO).toList();
    }

    @Mapping(ignore = true, target = "enabled")
    @Mapping(target = "resourceFile.bpmn", ignore = true)
    public abstract BpmnVersion toEntity(BpmnDTO bpmnDTO);

    public BpmnVersion toEntityUpgrade(BpmnUpgradeDto bpmnUpgradeDto, Long version, String definitionKey) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType(bpmnUpgradeDto.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(FileUtilities.calculateSha256(bpmnUpgradeDto.getFile()));
        bpmnVersion.setDeployedFileName(bpmnUpgradeDto.getFilename().concat(".").concat(S3ResourceTypeEnum.BPMN.getExtension()));
        bpmnVersion.setEnabled(true);
        bpmnVersion.setModelVersion(version);
        bpmnVersion.setDefinitionKey(definitionKey);
        bpmnVersion.setBpmnId(bpmnUpgradeDto.getUuid());
        return bpmnVersion;
    }
}
