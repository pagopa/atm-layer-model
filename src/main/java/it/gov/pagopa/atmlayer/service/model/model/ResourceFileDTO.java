package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ResourceFileDTO {
    private UUID id;
    private S3ResourceTypeEnum resourceType;
    @Schema(format = "byte", maxLength = 255)
    private String storageKey;
    @Schema(format = "byte", maxLength = 255)
    private String fileName;
    @Schema(format = "byte", maxLength = 255)
    private String extension;
    @Schema(description = "Creation Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp createdAt;
    @Schema(description = "Last Update Timestamp", format = "timestamp", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp lastUpdatedAt;
    @Schema(format = "byte", maxLength = 255)
    private String createdBy;
    @Schema(format = "byte", maxLength = 255)
    private String lastUpdatedBy;
}
