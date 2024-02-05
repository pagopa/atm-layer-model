package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnProcessDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
import jakarta.ws.rs.core.Response;
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

    public BpmnFrontEndDTO toFrontEndDTO(BpmnVersion bpmnVersion) {
        if (bpmnVersion.getResourceFile() == null) {
            String errorMessage = String.format("No resource file saved for bpmn with Id = %s and version = %s", bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion());
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.ATMLM_500);
        }
        BpmnFrontEndDTO bpmnFrontEndDTO = new BpmnFrontEndDTO();
        bpmnFrontEndDTO.setBpmnId(bpmnVersion.getBpmnId());
        bpmnFrontEndDTO.setModelVersion(bpmnVersion.getModelVersion());
        bpmnFrontEndDTO.setDeployedFileName(bpmnVersion.getDeployedFileName());
        bpmnFrontEndDTO.setDefinitionKey(bpmnVersion.getDefinitionKey());
        bpmnFrontEndDTO.setFunctionType(bpmnVersion.getFunctionType());
        bpmnFrontEndDTO.setStatus(bpmnVersion.getStatus());
        bpmnFrontEndDTO.setSha256(bpmnVersion.getSha256());
        bpmnFrontEndDTO.setEnabled(bpmnVersion.getEnabled());
        bpmnFrontEndDTO.setDefinitionVersionCamunda(bpmnVersion.getDefinitionVersionCamunda());
        bpmnFrontEndDTO.setCamundaDefinitionId(bpmnVersion.getCamundaDefinitionId());
        bpmnFrontEndDTO.setDescription(bpmnVersion.getDescription());
        bpmnFrontEndDTO.setResourceId(bpmnVersion.getResourceFile().getId());
        bpmnFrontEndDTO.setResourceType(bpmnVersion.getResourceFile().getResourceType());
        bpmnFrontEndDTO.setStorageKey(bpmnVersion.getResourceFile().getStorageKey());
        bpmnFrontEndDTO.setFileName(bpmnVersion.getResourceFile().getFileName());
        bpmnFrontEndDTO.setExtension(bpmnVersion.getResourceFile().getExtension());
        bpmnFrontEndDTO.setResourceCreatedAt(bpmnVersion.getResourceFile().getCreatedAt());
        bpmnFrontEndDTO.setResourceLastUpdatedAt(bpmnVersion.getResourceFile().getLastUpdatedAt());
        bpmnFrontEndDTO.setResourceCreatedBy(bpmnVersion.getResourceFile().getCreatedBy());
        bpmnFrontEndDTO.setResourceLastUpdatedBy(bpmnVersion.getResourceFile().getLastUpdatedBy());
        bpmnFrontEndDTO.setResource(bpmnVersion.getResource());
        bpmnFrontEndDTO.setDeploymentId(bpmnVersion.getDeploymentId());
        bpmnFrontEndDTO.setCreatedAt(bpmnVersion.getCreatedAt());
        bpmnFrontEndDTO.setLastUpdatedAt(bpmnVersion.getLastUpdatedAt());
        bpmnFrontEndDTO.setCreatedBy(bpmnVersion.getCreatedBy());
        bpmnFrontEndDTO.setLastUpdatedBy(bpmnVersion.getCreatedBy());
        return bpmnFrontEndDTO;
    }

    public List<BpmnDTO> toDTOList(List<BpmnVersion> list) {
        return list.stream().map(this::toDTO).toList();
    }

    public PageInfo<BpmnFrontEndDTO> toFrontEndDTOListPaged(PageInfo<BpmnVersion> input) {
        return new PageInfo<>(input.getPage(), input.getLimit(), input.getItemsFound(), input.getTotalPages(), toFrontEndDTOList(input.getResults()));
    }

    public List<BpmnFrontEndDTO> toFrontEndDTOList(List<BpmnVersion> list) {
        return list.stream().map(this::toFrontEndDTO).toList();
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
