package it.gov.pagopa.atml.mil.integration.utils;

import jakarta.validation.ConstraintViolation;

import java.util.List;
import java.util.Set;

public interface ConstraintViolationMappingUtils {

    List<String> extractErrorMessages(Set<ConstraintViolation<?>> errors);

    String extractErrorMessage(ConstraintViolation<?> error);
}
