package it.gov.pagopa.atml.mil.integration.model;

import it.gov.pagopa.atml.mil.integration.constraint.BankKeyConstraint;
import it.gov.pagopa.atml.mil.integration.enumeration.FunctionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class Metadata {
    @NotNull
    private FunctionEnum function; //enum
    @NotNull
    private String fileName; //no extension
    @NotNull
    private String bpmnKey;
    @BankKeyConstraint
    private List<BankKey> bankKeyList;
}
