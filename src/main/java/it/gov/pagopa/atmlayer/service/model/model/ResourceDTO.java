package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResourceDTO {
    private UUID resourceId;
    private String sha256;
    NoDeployableResourceType noDeployableResourceType;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private String createdBy;
    private String lastUpdatedBy;
}
