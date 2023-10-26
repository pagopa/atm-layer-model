package it.gov.pagopa.atmlayer.service.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
public class BpmnBankConfigPK implements Serializable {
    @Serial
    private static final long serialVersionUID = -9140657073094910845L;

    @NotNull(message = "bpmn id cannot be null")
    private UUID bpmnId;

    @NotNull(message = "bpmn model version cannot be null")
    private int bpmnModelVersion;

    @NotNull(message = "acquirer id cannot be null")
    private String acquirerId;

    private String branchId;

    private String terminalId;
}
