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
public class DeployedBPMNProcessDefinitionDto {
    private String id;
    private String key;
    private String category;
    private String description;
    private String name;
    private Integer version;
    private String resource;
    private UUID deploymentId;
    private String diagram;
    private Boolean suspended;
    private String tenantId;
    private String versionTag;
    private int historyTimeToLeave;
    private Boolean startableInTasklist;
}
