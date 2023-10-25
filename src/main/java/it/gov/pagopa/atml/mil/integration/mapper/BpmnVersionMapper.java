package it.gov.pagopa.atml.mil.integration.mapper;

import it.gov.pagopa.atml.mil.integration.entity.BpmnVersion;
import it.gov.pagopa.atml.mil.integration.model.CreationMetadata;
import it.gov.pagopa.atml.mil.integration.dto.BpmnCreationDto;
import org.mapstruct.Mapper;

@Mapper
public interface BpmnVersionMapper {

    BpmnCreationDto toDto (BpmnVersion bpmnVersion);

    CreationMetadata toEntity (BpmnCreationDto bpmnCreationDto);
}
