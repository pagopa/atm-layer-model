package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
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
public class ResourceFrontEndDTO {
    private UUID resourceId;
    @Size(max = 255)
    private String sha256;
    private Boolean enabled;
    NoDeployableResourceType noDeployableResourceType;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    @Size(max = 255)
    private String createdBy;
    @Size(max = 255)
    private String lastUpdatedBy;
    @Size(max = 255)
    private String cdnUrl;
    private UUID resourceFileId;
    private S3ResourceTypeEnum resourceType;
    @Size(max = 255)
    private String storageKey;
    @Size(max = 255)
    private String fileName;
    @Size(max = 255)
    private String extension;
    @Schema(description = "Creation Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "2023-11-03T14:18:36.635+00:00")
    private Timestamp resourceFileCreatedAt;
    @Schema(description = "Last Update Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "2023-11-03T14:18:36.635+00:00")
    private Timestamp resourceFileLastUpdatedAt;
    @Size(max = 255)
    private String resourceFileCreatedBy;
    @Size(max = 255)
    private String resourceFileLastUpdatedBy;
}
