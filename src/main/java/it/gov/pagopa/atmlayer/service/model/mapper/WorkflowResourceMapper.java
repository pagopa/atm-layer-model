package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Mapper(componentModel = "cdi")
public abstract class WorkflowResourceMapper {

    @Mapping(ignore = true, target = "file")
    @Mapping(ignore = true, target = "filename")
    public abstract WorkflowResourceCreationDto toDtoCreation(WorkflowResourceCreationDto workflowCreationDto);

    public WorkflowResource toEntityCreation(WorkflowResourceCreationDto workflowCreationDto) throws NoSuchAlgorithmException, IOException {
        WorkflowResource workflowResource = new WorkflowResource();
        workflowResource.setFunctionType(workflowCreationDto.getFunctionType());
        workflowResource.setStatus(StatusEnum.CREATED);
        workflowResource.setSha256(BpmnUtils.calculateSha256(workflowCreationDto.getFile()));
        workflowResource.setDeployedFileName(workflowCreationDto.getFilename().concat(".").concat(workflowCreationDto.getResourceType().toString()));
        workflowResource.setResourceType(workflowCreationDto.getResourceType());
        return workflowResource;
    }

    public abstract WorkflowResourceDTO toDTO(WorkflowResource workflowResource);

//    @Mapping(ignore = true, target = "enabled")
//    @Mapping(target = "resourceFile.dmn",ignore = true)
//    public abstract WorkflowResource toEntity(WorkflowResourceDTO workflowResourceDTO);
}
