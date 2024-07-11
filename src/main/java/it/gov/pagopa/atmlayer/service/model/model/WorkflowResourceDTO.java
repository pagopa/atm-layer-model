package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class WorkflowResourceDTO {
    private UUID workflowResourceId;
    @Size(max=255)
    private String deployedFileName;
    @Size(max=255)
    private String definitionKey;
    private StatusEnum status;
    @Size(max=255)
    private String sha256;
    private Boolean enabled;
    @Schema(minimum = "1", maximum = "10000")
    private Integer definitionVersionCamunda;
    @Size(max=255)
    private String camundaDefinitionId;
    @Size(max=255)
    private String description;
    private ResourceFileDTO resourceFile;
    @Size(max=255)
    private String resource;
    private DeployableResourceType resourceType;
    private UUID deploymentId;
    @Schema(description = "Creation Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp createdAt;
    @Schema(description = "Last Update Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp lastUpdatedAt;
    @Size(max=255)
    private String createdBy;
    @Size(max=255)
    private String lastUpdatedBy;

}
