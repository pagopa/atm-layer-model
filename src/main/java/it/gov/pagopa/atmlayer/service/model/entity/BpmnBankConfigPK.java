package it.gov.pagopa.atmlayer.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BpmnBankConfigPK implements Serializable {

    @NotNull(message = "bpmn id cannot be null")
    @Column(name = "bpmn_id")
    private UUID bpmnId;

    @NotNull(message = "bpmn model version cannot be null")
    @Column(name = "bpmn_model_version")
    private Long bpmnModelVersion;

    @NotNull(message = "acquirer id cannot be null")
    @Column(name = "acquirer_id")
    private String acquirerId;
    @Column(name = "branch_id")
    private String branchId;
    @Column(name = "terminal_id")
    private String terminalId;
}