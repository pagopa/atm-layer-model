package it.gov.pagopa.atml.mil.integration.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BankKey {

    @NotNull(message = "The acquirerId cannot be null")
    private String acquirerId;

    private String branchId;

    private List<String> terminalId;
}
