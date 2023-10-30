package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    CREATED("CREATED"),
    WAITING_DEPLOY("DEPLOY_IN_PROGRESS"),
    DEPLOYED("DEPLOYED"),
    DEPLOY_ERROR("DEPLOY_ERROR");

    private String value;

}
