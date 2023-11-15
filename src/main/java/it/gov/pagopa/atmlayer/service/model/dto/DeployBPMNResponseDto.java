package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeployBPMNResponseDto {
    private List<LinkDto> links;
    private String id;
    private String name;
    private String source;
    private String deploymentTime;
    private String tenantId;
    private Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions;
    private String deployedCaseDefinitions;
    private Map<String,DeployedDMNDecisionDefinitionDto> deployedDecisionDefinitions;
    private String deployedDecisionRequirementsDefinitions;
}
