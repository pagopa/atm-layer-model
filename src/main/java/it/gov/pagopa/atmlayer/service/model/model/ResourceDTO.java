package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ResourceDTO {
    private UUID resourceId;
    @Schema(format = "byte", maxLength = 255)
    private String sha256;
    private Boolean enabled;
    NoDeployableResourceType noDeployableResourceType;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    @Schema(format = "byte", maxLength = 255)
    private String createdBy;
    @Schema(format = "byte", maxLength = 255)
    private String lastUpdatedBy;
    @Schema(format = "byte", maxLength = 255)
    private String cdnUrl;
    private ResourceFileDTO resourceFile;
    @Schema(format = "byte", maxLength = 255)
    private String description;
}
