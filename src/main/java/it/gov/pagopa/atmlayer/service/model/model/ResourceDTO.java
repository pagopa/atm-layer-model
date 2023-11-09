package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ResourceDTO {
    private UUID resourceId;
    private String sha256;
    ResourceTypeEnum resourceTypeEnum;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private String createdBy;
    private String lastUpdatedBy;
}
