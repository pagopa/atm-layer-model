package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BankKeyDto {

    @NotNull(message = "The acquirerId cannot be null")
    private String acquirerId;

    private List<BranchDto> branches;
}
