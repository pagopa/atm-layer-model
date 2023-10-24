package it.gov.pagopa.atml.mil.integration.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BankKey {

    @NotNull
    private String acquirerId;

    @NotEmpty
    private String branchId;

    private List<String> terminalId;
}
