package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.Getter;

@Getter
public enum AppErrorType {
    GENERIC,
    VALIDATION,
    CONSTRAINT_VIOLATION,
    NOT_EXISTING_REFERENCED_ENTITY,
    NOT_VALID_REFERENCED_ENTITY,
    NOT_DEPLOYABLE_STATUS,
    NOT_DEPLOYED_STATUS,
    INVALID_FUNCTION_TYPE,
    NOT_DELETABLE,
    INTERNAL
}