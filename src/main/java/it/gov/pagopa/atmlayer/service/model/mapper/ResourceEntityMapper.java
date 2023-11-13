package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import jakarta.inject.Inject;
import org.mapstruct.Mapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Mapper(componentModel = "cdi")
public abstract class ResourceEntityMapper {

    @Inject
    ResourceEntityStorageService resourceEntityStorageService;

    public ResourceEntity toEntityCreation(ResourceCreationDto resourceCreationDto) throws NoSuchAlgorithmException, IOException {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setSha256(BpmnUtils.calculateSha256(resourceCreationDto.getFile()));
        resourceEntity.setNoDeployableResourceType(resourceCreationDto.getResourceType());
        resourceEntity.setFileName(resourceCreationDto.getFilename());
        resourceEntity.setStorageKey(resourceEntityStorageService.calculateStorageKey(
                resourceCreationDto.getResourceType(),resourceCreationDto.getPath(),resourceCreationDto.getFilename()
        ));
        return resourceEntity;
    }

    public abstract ResourceDTO toDTO(ResourceEntity resourceEntity);
}
