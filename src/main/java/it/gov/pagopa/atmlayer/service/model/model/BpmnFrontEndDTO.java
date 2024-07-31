package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BpmnFrontEndDTO {
    private UUID bpmnId;
    @Schema(minimum = "1", maximum = "10000")
    private Long modelVersion;
    @Schema(format = "byte", maxLength = 255)
    private String deployedFileName;
    @Schema(format = "byte", maxLength = 255)
    private String definitionKey;
    @Schema(format = "byte", maxLength = 255)
    private String functionType;
    private StatusEnum status;
    @Schema(format = "byte", maxLength = 255)
    private String sha256;
    private Boolean enabled;
    @Schema(minimum = "1", maximum = "10000")
    private Integer definitionVersionCamunda;
    @Schema(format = "byte", maxLength = 255)
    private String camundaDefinitionId;
    @Schema(format = "byte", maxLength = 255)
    private String description;
    private UUID resourceId;
    private S3ResourceTypeEnum resourceType;
    @Schema(format = "byte", maxLength = 255)
    private String storageKey;
    @Schema(format = "byte", maxLength = 255)
    private String fileName;
    @Schema(format = "byte", maxLength = 255)
    private String extension;
    @Schema(description = "Creation Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp resourceCreatedAt;
    @Schema(description = "Last Update Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp resourceLastUpdatedAt;
    @Schema(format = "byte", maxLength = 255)
    private String resourceCreatedBy;
    @Schema(format = "byte", maxLength = 255)
    private String resourceLastUpdatedBy;
    @Schema(format = "byte", maxLength = 255)
    private String resource;
    private UUID deploymentId;
    @Schema(description = "Creation Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp createdAt;
    @Schema(description = "Last Update Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp lastUpdatedAt;
    @Schema(format = "byte", maxLength = 255)
    private String createdBy;
    @Schema(format = "byte", maxLength = 255)
    private String lastUpdatedBy;
}
