package it.gov.pagopa.atml.mil.integration.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    CREATED("CREATED"),
    WAITING_DEPLOY("WAITING_DEPLOY"),
    DEPLOYED("DEPLOYED");

    private String value;

}
