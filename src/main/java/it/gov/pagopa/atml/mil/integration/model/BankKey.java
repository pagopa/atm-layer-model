package it.gov.pagopa.atml.mil.integration.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BankKey {

    @NotBlank
    private String acquirerId;

    @Nullable
    private String branchId;

    private List<String> terminalId;
}
