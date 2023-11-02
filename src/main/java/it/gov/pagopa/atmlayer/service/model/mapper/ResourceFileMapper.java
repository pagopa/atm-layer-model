package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ResourceFileMapper {

    ResourceFileDTO toDTO(ResourceFile bpmnVersion);
    @Mapping(ignore = true, target = "bpmn")
    ResourceFile toEntity(ResourceFileDTO bpmnDTO);
}
