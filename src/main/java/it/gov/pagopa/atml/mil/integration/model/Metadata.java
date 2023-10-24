package it.gov.pagopa.atml.mil.integration.model;

import lombok.Data;

import java.util.List;

@Data
public class Metadata {

    private String function; //enum

    private String fileName; //no extension

    private String bpmnKey;

    private List<BankKey> associations;
}
