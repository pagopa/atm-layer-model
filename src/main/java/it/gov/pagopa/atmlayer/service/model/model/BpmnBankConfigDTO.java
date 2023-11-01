package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private FunctionTypeEnum functionType;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private String createdBy;
    private String lastUpdatedBy;
}
