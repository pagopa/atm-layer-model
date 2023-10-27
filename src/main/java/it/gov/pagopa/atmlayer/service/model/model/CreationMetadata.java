package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreationMetadata {
    @NotNull(message = "deployed file name cannot be null")
    private String deployedFileName;
    @NotNull(message = "definition key file name cannot be null")
    private String definitionKey;
    @NotNull(message = "function type file name cannot be null")
    private FunctionEnum functionType;
}