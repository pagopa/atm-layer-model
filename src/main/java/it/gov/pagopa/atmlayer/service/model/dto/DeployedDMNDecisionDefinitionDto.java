package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeployedDMNDecisionDefinitionDto {
    private String id;
    private String key;
    private String category;
    private String name;
    private Integer version;
    private String resource;
    private UUID deploymentId;
    private String tenantId;
    private String decisionRequirementsDefinitionId;
    private String decisionRequirementsDefinitionKey;
    private int historyTimeToLive;
    private String versionTag;
}
