package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BpmnAssociationDto {

    private UUID defaultTemplateId;

    private Long defaultTemplateVersion;

    private List<BranchConfigs> branchesConfigs;
}
