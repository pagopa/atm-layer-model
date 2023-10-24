package it.gov.pagopa.atml.mil.integration.model;

import java.util.List;

public class Metadata {

    private String function; //enum

    private String fileName; //no extension

    private String bpmnKey;

    private List<BankAccountKey> associations;
}
