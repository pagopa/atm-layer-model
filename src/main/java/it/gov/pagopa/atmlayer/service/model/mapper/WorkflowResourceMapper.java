package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
import jakarta.ws.rs.core.Response;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class WorkflowResourceMapper {
    @Mapping(ignore = true, target = "file")
    @Mapping(ignore = true, target = "filename")
    public abstract WorkflowResourceCreationDto toDtoCreation(WorkflowResourceCreationDto workflowCreationDto);

    public WorkflowResource toEntityCreation(WorkflowResourceCreationDto workflowCreationDto) throws NoSuchAlgorithmException, IOException {
        WorkflowResource workflowResource = new WorkflowResource();
        workflowResource.setStatus(StatusEnum.CREATED);
        workflowResource.setSha256(FileUtilities.calculateSha256(workflowCreationDto.getFile()));
        workflowResource.setDeployedFileName(workflowCreationDto.getFilename().concat(".").concat(workflowCreationDto.getResourceType().toString()));
        workflowResource.setResourceType(workflowCreationDto.getResourceType());
        return workflowResource;
    }

    public abstract WorkflowResourceDTO toDTO(WorkflowResource workflowResource);

    public List<WorkflowResourceDTO> toDTOList(List<WorkflowResource> list) {
        return list.stream().map(this::toDTO).toList();
    }

    public WorkflowResourceFrontEndDTO toFrontEndDTO(WorkflowResource workflowResource) {
        if(workflowResource.getResourceFile()==null){
            String errorMessage = String.format("No resource file saved for workflow resource with Id = %s",workflowResource.getWorkflowResourceId());
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.ATMLM_500);
        }
        WorkflowResourceFrontEndDTO wrFrontEndDTO = new WorkflowResourceFrontEndDTO();
        wrFrontEndDTO.setWorkflowResourceId(workflowResource.getWorkflowResourceId());
        wrFrontEndDTO.setDeployedFileName(workflowResource.getDeployedFileName());
        wrFrontEndDTO.setDefinitionKey(workflowResource.getDefinitionKey());
        wrFrontEndDTO.setStatus(workflowResource.getStatus());
        wrFrontEndDTO.setSha256(workflowResource.getSha256());
        wrFrontEndDTO.setDefinitionVersionCamunda(workflowResource.getDefinitionVersionCamunda());
        wrFrontEndDTO.setCamundaDefinitionId(workflowResource.getCamundaDefinitionId());
        wrFrontEndDTO.setDescription(workflowResource.getDescription());
        wrFrontEndDTO.setResourceId(workflowResource.getResourceFile().getId());
        wrFrontEndDTO.setResourceS3Type(workflowResource.getResourceFile().getResourceType());
        wrFrontEndDTO.setStorageKey(workflowResource.getResourceFile().getStorageKey());
        wrFrontEndDTO.setFileName(workflowResource.getResourceFile().getFileName());
        wrFrontEndDTO.setExtension(workflowResource.getResourceFile().getExtension());
        wrFrontEndDTO.setResourceCreatedAt(workflowResource.getResourceFile().getCreatedAt());
        wrFrontEndDTO.setResourceLastUpdatedAt(workflowResource.getResourceFile().getLastUpdatedAt());
        wrFrontEndDTO.setResourceCreatedBy(workflowResource.getResourceFile().getCreatedBy());
        wrFrontEndDTO.setResourceLastUpdatedBy(workflowResource.getResourceFile().getLastUpdatedBy());
        wrFrontEndDTO.setResource(workflowResource.getResource());
        wrFrontEndDTO.setResourceType(workflowResource.getResourceType());
        wrFrontEndDTO.setDeploymentId(workflowResource.getDeploymentId());
        wrFrontEndDTO.setCreatedAt(workflowResource.getCreatedAt());
        wrFrontEndDTO.setLastUpdatedAt(workflowResource.getLastUpdatedAt());
        wrFrontEndDTO.setCreatedBy(workflowResource.getCreatedBy());
        wrFrontEndDTO.setLastUpdatedBy(workflowResource.getLastUpdatedBy());
        return wrFrontEndDTO;
    }

    public List<WorkflowResourceFrontEndDTO> toFrontEndDTOList(List<WorkflowResource> list){
        return list.stream().map(this::toFrontEndDTO).toList();
    }

    public PageInfo<WorkflowResourceFrontEndDTO> toFrontEndDTOListPaged(PageInfo<WorkflowResource> page){
        return new PageInfo<>(page.getPage(),page.getLimit(), page.getItemsFound(), page.getTotalPages(),toFrontEndDTOList(page.getResults()));
    }
}
