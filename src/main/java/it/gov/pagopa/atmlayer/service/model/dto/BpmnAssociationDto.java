package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BpmnAssociationDto {

    private UUID defaultTemplateId;

    private Long defaultTemplateVersion;

    private List<BranchConfigs> branchesConfigs;
}
