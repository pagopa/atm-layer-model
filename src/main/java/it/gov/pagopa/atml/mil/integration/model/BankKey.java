package it.gov.pagopa.atml.mil.integration.model;

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

    @NonNull
    private String acquirerId;

    @Nullable
    private String branchId;

    @Nullable
    private List<String> terminalId;
}
