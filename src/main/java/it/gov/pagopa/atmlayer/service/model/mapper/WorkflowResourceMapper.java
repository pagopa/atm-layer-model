package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public abstract class WorkflowResourceMapper {

    @Mapping(ignore = true, target = "file")
    @Mapping(ignore = true, target = "filename")
    public abstract WorkflowResourceCreationDto toDtoCreation(WorkflowResourceCreationDto workflowCreationDto);

    public WorkflowResource toEntityCreation(WorkflowResourceCreationDto workflowCreationDto) throws NoSuchAlgorithmException, IOException {
        WorkflowResource workflowResource = new WorkflowResource();
        workflowResource.setStatus(StatusEnum.CREATED);
        workflowResource.setSha256(FileUtils.calculateSha256(workflowCreationDto.getFile()));
        workflowResource.setDeployedFileName(workflowCreationDto.getFilename().concat(".").concat(workflowCreationDto.getResourceType().toString()));
        workflowResource.setResourceType(workflowCreationDto.getResourceType());
        return workflowResource;
    }

    public abstract WorkflowResourceDTO toDTO(WorkflowResource workflowResource);

    public List<WorkflowResourceDTO> toDTOList(List<WorkflowResource> list) {
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
