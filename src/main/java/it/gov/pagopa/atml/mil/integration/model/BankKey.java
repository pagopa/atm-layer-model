package it.gov.pagopa.atml.mil.integration.model;

import it.gov.pagopa.atml.mil.integration.constraint.BankKeyConstraint;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class BankKey {

    @NotNull(message = "The acquirerId cannot be null")
    private String acquirerId;

    private String branchId;

    private List<String> terminalId;
}
