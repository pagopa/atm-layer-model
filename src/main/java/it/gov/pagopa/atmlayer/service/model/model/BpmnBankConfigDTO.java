package it.gov.pagopa.atmlayer.service.model.model;

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
    private Long bpmnModelVersion;
    private String acquirerId;
    private String branchId;
    private String terminalId;
    private String functionType;
    @Schema(description = "Creation Timestamp", format = "date-time", pattern = "DD/MM/YYYY", example = "2023-11-03T14:18:36.635+00:00")
    private Timestamp createdAt;
    @Schema(description = "Last Update Timestamp", format = "date-time", pattern = "DD/MM/YYYY", example = "2023-11-03T14:18:36.635+00:00")
    private Timestamp lastUpdatedAt;
    private String createdBy;
    private String lastUpdatedBy;
}
