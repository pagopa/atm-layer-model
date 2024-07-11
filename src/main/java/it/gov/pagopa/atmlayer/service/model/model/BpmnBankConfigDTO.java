package it.gov.pagopa.atmlayer.service.model.model;

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
public class BpmnBankConfigDTO {
    private UUID bpmnId;
    @Schema(minimum = "1", maximum = "10000")
    private Long bpmnModelVersion;
    @Size(max = 255)
    private String acquirerId;
    @Size(max = 255)
    private String branchId;
    @Size(max = 255)
    private String terminalId;
    @Size(max = 255)
    private String functionType;
    @Schema(description = "Creation Timestamp", format = "date-time", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp createdAt;
    @Schema(description = "Last Update Timestamp", format = "date-time", pattern = "DD/MM/YYYY", example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp lastUpdatedAt;
    @Size(max = 255)
    private String createdBy;
    @Size(max = 255)
    private String lastUpdatedBy;
}
