package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankConfigDeleteDto {
    @NotEmpty
    private UUID bpmnId;
    @NotEmpty
    private Long bpmnModelVersion;
    @NotEmpty
    private String acquirerId;
    private String branchId;
    private String terminalId;
}
