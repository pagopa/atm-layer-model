package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.Getter;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.CONSTRAINT_VIOLATION;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.GENERIC;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.INTERNAL;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_DEPLOYABLE_STATUS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_EXISTING_REFERENCED_ENTITY;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_VALID_REFERENCED_ENTITY;

/**
 * Enumeration for application error codes and messages
 */
@Getter
public enum AppErrorCodeEnum {

    ATMLM_500("ATMLM_500", "An unexpected error has occurred, see logs for more info", GENERIC),
    BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST("ATMLM_4000001", "A BPMN file with the same content already exists", CONSTRAINT_VIOLATION),
    BPMN_FILE_DOES_NOT_EXIST("ATMLM_4000002", "The referenced BPMN file does not exist", NOT_EXISTING_REFERENCED_ENTITY),
    BPMN_FILE_NOT_DEPLOYED("ATMLM_4000003", "The referenced BPMN file is not deployed", NOT_EXISTING_REFERENCED_ENTITY),
    BPMN_FILE_CANNOT_BE_DEPLOYED("ATMLM_4000004", "The referenced BPMN file can not be deployed", NOT_DEPLOYABLE_STATUS),
    MULTIPLE_BPMN_FILE_FOR_SINGLE_CONFIGURATION("ATMLM_4000005", "Multiple BPMN file found for a single configuration", INTERNAL),
    NO_BPMN_FOUND_FOR_CONFIGURATION("ATMLM_4000006", "No runnable BPMN found for configiration", NOT_VALID_REFERENCED_ENTITY);

    private final String errorCode;
    private final String errorMessage;
    private final AppErrorType type;

    AppErrorCodeEnum(String errorCode, String errorMessage, AppErrorType type) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.type = type;
    }
}
