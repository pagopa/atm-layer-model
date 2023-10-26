package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.model.CreationMetadata;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import org.mapstruct.Mapper;

@Mapper
public interface BpmnVersionMapper {

    BpmnCreationDto toDto (BpmnVersion bpmnVersion);

    CreationMetadata toEntity (BpmnCreationDto bpmnCreationDto);
}
