package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.Getter;

@Getter
public enum AppErrorType {
    GENERIC,
    VALIDATION,
    INTERNAL,
    CONSTRAINT_VIOLATION,
    NOT_EXISTING_REFERENCED_ENTITY
}