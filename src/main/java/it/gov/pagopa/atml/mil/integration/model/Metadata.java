package it.gov.pagopa.atml.mil.integration.model;

import it.gov.pagopa.atml.mil.integration.constraint.BankKeyConstraint;
import it.gov.pagopa.atml.mil.integration.enumeration.FunctionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class Metadata {
    @NonNull
    private FunctionEnum function; //enum
    @NonNull
    private String fileName; //no extension
    @NonNull
    private String bpmnKey;
    @BankKeyConstraint
    private List<BankKey> bankKeyList;
}
