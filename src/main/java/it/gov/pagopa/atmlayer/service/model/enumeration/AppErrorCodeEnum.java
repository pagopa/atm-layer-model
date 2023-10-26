package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.Getter;

/**
 * Enumeration for application error codes and messages
 */
@Getter
public enum AppErrorCodeEnum {

    ATML_MI_500("ATML_MI_500", "An unexpected error has occurred, see logs for more info");

    private final String errorCode;
    private final String errorMessage;

    AppErrorCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
