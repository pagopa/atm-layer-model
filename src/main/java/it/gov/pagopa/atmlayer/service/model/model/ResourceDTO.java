package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ResourceDTO {
    private UUID resourceId;
    private String sha256;
    S3ResourceTypeEnum s3ResourceTypeEnum;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private String createdBy;
    private String lastUpdatedBy;
}
