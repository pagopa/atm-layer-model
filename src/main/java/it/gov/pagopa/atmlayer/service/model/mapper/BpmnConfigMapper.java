package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BpmnConfigMapper {

    @Mapping(source = "bpmnBankConfigPK.bpmnId", target = "bpmnId")
    @Mapping(source = "bpmnBankConfigPK.bpmnModelVersion", target = "bpmnModelVersion")
    @Mapping(source = "bpmnBankConfigPK.acquirerId", target = "acquirerId")
    @Mapping(source = "bpmnBankConfigPK.branchId", target = "branchId")
    @Mapping(source = "bpmnBankConfigPK.terminalId", target = "terminalId")
    @Named("toBpmnBankConfigDTO")
    BpmnBankConfigDTO toDTO(BpmnBankConfig bpmnBankConfig);

    @Mapping(source = "bpmnId", target = "bpmnBankConfigPK.bpmnId")
    @Mapping(source = "bpmnModelVersion", target = "bpmnBankConfigPK.bpmnModelVersion")
    @Mapping(source = "acquirerId", target = "bpmnBankConfigPK.acquirerId")
    @Mapping(source = "branchId", target = "bpmnBankConfigPK.branchId")
    @Mapping(source = "terminalId", target = "bpmnBankConfigPK.terminalId")
    BpmnBankConfig toEntity(BpmnBankConfigDTO bankConfigDTO);

    @IterableMapping(qualifiedByName = "toBpmnBankConfigDTO")
    Collection<BpmnBankConfigDTO> toDTOList(Collection<BpmnBankConfig> bpmnBankConfig);
}
