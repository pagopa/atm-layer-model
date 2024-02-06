package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFileDTO;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFrontEndDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ResourceFileMapper {

    ResourceFileDTO toDTO(ResourceFile resourceFile);
    @Mapping(ignore = true, target = "bpmn")
    ResourceFile toEntity(ResourceFileDTO resourceFileDTO);
}
