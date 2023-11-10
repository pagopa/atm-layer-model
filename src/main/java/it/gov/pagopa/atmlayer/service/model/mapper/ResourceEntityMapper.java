package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import org.mapstruct.Mapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Mapper(componentModel = "cdi")
public abstract class ResourceEntityMapper {
    public ResourceEntity toEntityCreation(ResourceCreationDto resourceCreationDto) throws NoSuchAlgorithmException, IOException {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setSha256(BpmnUtils.calculateSha256(resourceCreationDto.getFile()));
        resourceEntity.setWorkflowResourceTypeEnum(resourceCreationDto.getResourceType());
        resourceEntity.setFileName(resourceCreationDto.getFilename());
        return resourceEntity;
    }

    //TODO: IMPLEMENT toDto(ResourceEntity resourceEntity) METHOD
    public abstract ResourceEntityDTO toDTO(ResourceEntity resourceEntity);
}
