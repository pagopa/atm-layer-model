package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    CREATED("CREATED"),
    WAITING_DEPLOY("WAITING_DEPLOY"),
    DEPLOYED("DEPLOYED");

    private String value;

}
