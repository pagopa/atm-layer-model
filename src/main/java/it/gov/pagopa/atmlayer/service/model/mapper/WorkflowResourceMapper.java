package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
        workflowResource.setEnabled(true);
        return workflowResource;
    }

    public abstract WorkflowResourceDTO toDTO(WorkflowResource workflowResource);

    public List<WorkflowResourceDTO> toDTOList(List<WorkflowResource> list) {
        return list.stream().map(this::toDTO).toList();
    }

    @Mapping(source = "resourceFile.id", target = "resourceId")
    @Mapping(source = "resourceFile.resourceType", target = "resourceS3Type")
    @Mapping(source = "resourceFile.storageKey", target = "storageKey")
    @Mapping(source = "resourceFile.fileName", target = "fileName")
    @Mapping(source = "resourceFile.extension", target = "extension")
    @Mapping(source = "resourceFile.createdAt", target = "resourceCreatedAt")
    @Mapping(source = "resourceFile.lastUpdatedAt", target = "resourceLastUpdatedAt")
    @Mapping(source = "resourceFile.createdBy", target = "resourceCreatedBy")
    @Mapping(source = "resourceFile.lastUpdatedAt", target = "resourceLastUpdatedBy")
    @Named("toWorkflowResourceFrontEndDTO")
    public abstract WorkflowResourceFrontEndDTO toFrontEndDTO(WorkflowResource workflowResource);

    @IterableMapping(qualifiedByName = "toWorkflowResourceFrontEndDTO")
    @Named("toWorkflowResourceFrontEndDTOList")
    public abstract List<WorkflowResourceFrontEndDTO> toFrontEndDTOList(List<WorkflowResource> list);

    @Mapping(source = "results", target = "results", qualifiedByName = "toWorkflowResourceFrontEndDTOList")
    public abstract PageInfo<WorkflowResourceFrontEndDTO> toFrontEndDTOListPaged(PageInfo<WorkflowResource> page);
}
