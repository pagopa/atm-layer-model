package it.gov.pagopa.atml.mil.integration.model;

import it.gov.pagopa.atml.mil.integration.enumeration.FunctionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreationMetadata {
    @NotNull(message = "bpmn id cannot be null")
    private UUID bpmnId;
    @NotNull(message = "model version cannot be null")
    private int modelVersion;
    @NotNull(message = "deployed file name cannot be null")
    private String deployedFileName;
    @NotNull(message = "definition key file name cannot be null")
    private String definitionKey;
    @NotNull(message = "function type file name cannot be null")
    private FunctionEnum functionType;
}