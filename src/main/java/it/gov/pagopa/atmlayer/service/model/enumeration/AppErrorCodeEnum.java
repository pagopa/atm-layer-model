package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.Getter;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.CONSTRAINT_VIOLATION;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.GENERIC;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.INTERNAL;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.INVALID_FUNCTION_TYPE;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_DELETABLE;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_DEPLOYABLE_STATUS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_DEPLOYED_STATUS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_EXISTING_REFERENCED_ENTITY;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_UPGRADABLE;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_VALID_FILE;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.NOT_VALID_REFERENCED_ENTITY;

/**
 * Enumeration for application error codes and messages
 */
@Getter
public enum AppErrorCodeEnum {

    ATMLM_500("ATMLM_500", "An unexpected error has occurred, see logs for more info", GENERIC),
    BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST("ATMLM_4000001", "A BPMN file with the same content already exists", CONSTRAINT_VIOLATION),
    BPMN_FILE_DOES_NOT_EXIST("ATMLM_4000002", "The referenced BPMN file does not exist", NOT_EXISTING_REFERENCED_ENTITY),
    BPMN_FILE_NOT_DEPLOYED("ATMLM_4000003", "The referenced BPMN file is not deployed", NOT_DEPLOYED_STATUS),
    BPMN_FILE_CANNOT_BE_DEPLOYED("ATMLM_4000004", "The referenced BPMN file can not be deployed", NOT_DEPLOYABLE_STATUS),
    BPMN_FUNCTION_TYPE_DIFFERENT_FROM_REQUESTED("ATMLM_4000005", "The referenced BPMN file has a function type different from the requested", INVALID_FUNCTION_TYPE),
    BPMN_CANNOT_BE_DELETED_FOR_STATUS("ATMLM_4000006", "The referenced BPMN file can not be deleted in the actual state", NOT_DELETABLE),
    MULTIPLE_BPMN_FILE_FOR_SINGLE_CONFIGURATION("ATMLM_4000007", "Multiple BPMN file found for a single configuration", INTERNAL),
    NO_BPMN_FOUND_FOR_CONFIGURATION("ATMLM_4000008", "No runnable BPMN found for configuration", NOT_VALID_REFERENCED_ENTITY),
    NO_FILE_OR_STORAGE_KEY_FOUND_FOR_BPMN("ATMLM_4000009", "No storage key or file found for BPMN", NOT_VALID_REFERENCED_ENTITY),
    OBJECT_STORE_SAVE_FILE_ERROR("ATMLM_4000010", "Error on persisting file on Object Store ", INTERNAL),
    BPMN_FILE_CANNOT_BE_UPGRADED("ATMLM_4000011", "The referenced BPMN file can not be upgraded", NOT_UPGRADABLE),
    BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS("ATMLM_4000012","A BPMN file with the same Camunda definition key already exists", CONSTRAINT_VIOLATION),
    BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY("ATMLM_4000013","BPMN file does not have a definition key",NOT_VALID_FILE);

    private final String errorCode;
    private final String errorMessage;
    private final AppErrorType type;

    AppErrorCodeEnum(String errorCode, String errorMessage, AppErrorType type) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.type = type;
    }
}
