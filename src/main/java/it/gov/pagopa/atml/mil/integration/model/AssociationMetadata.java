package it.gov.pagopa.atml.mil.integration.model;

import it.gov.pagopa.atml.mil.integration.constraint.BankKeyConstraint;
import it.gov.pagopa.atml.mil.integration.enumeration.FunctionEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AssociationMetadata {
    @NotNull(message = "The function cannot be null")
    private FunctionEnum function; //enum
    @NotNull(message = "The filename cannot be null")
    private String fileName; //no extension
    @NotNull(message = "The BPMN key cannot be null")
    private String bpmnKey;
    @NotNull(message = "The Bank Key list cannot be null")
    private List<@BankKeyConstraint @Valid BankKey> bankKeyList;
}
