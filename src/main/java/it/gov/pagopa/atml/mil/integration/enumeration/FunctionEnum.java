package it.gov.pagopa.atml.mil.integration.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FunctionEnum {
    MENU("MENU"),
    SPONTANEOUS_PAYMENT("SPONTANEOUS_PAYMENT");

    private String value;
}
