package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ResourceDTO {
    private UUID resourceId;
    @Size(max=255)
    private String sha256;
    private Boolean enabled;
    NoDeployableResourceType noDeployableResourceType;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    @Size(max=255)
    private String createdBy;
    @Size(max=255)
    private String lastUpdatedBy;
    @Size(max=255)
    private String cdnUrl;
    private ResourceFileDTO resourceFile;
    @Size(max=255)
    private String description;
}
