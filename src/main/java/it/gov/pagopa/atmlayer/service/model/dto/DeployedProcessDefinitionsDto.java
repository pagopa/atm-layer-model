package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeployedProcessDefinitionsDto {
    private DeployedProcessInfoDto deployedProcessInfoDto;
    private String deployedCaseDefinitions;
    private String deployedDecisionDefinitions;
    private String deployedDecisionRequirementsDefinitions;
}
