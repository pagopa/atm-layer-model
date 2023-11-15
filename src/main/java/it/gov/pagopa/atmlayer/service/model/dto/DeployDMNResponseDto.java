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
public class DeployDMNResponseDto {
    private List<LinkDto> links;
    private UUID id;
    private String name;
    private String source;
    private String deploymentTime;
    private String tenantId;
    private String deployedProcessDefinitions;
    private String deployedCaseDefinitions;
    private Map<String,DeployedDMNDecisionDefinitionDto> deployedDecisionDefinitions;
    private String deployedDecisionRequirementsDefinitions;
}
