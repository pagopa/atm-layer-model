package it.gov.pagopa.atmlayer.service.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;

class ConstraintViolationMappingUtilsImplTest {


  @Test
  void testExtractErrorMessages() {
    ConstraintViolationMappingUtilsImpl constraintViolationMappingUtilsImpl = new ConstraintViolationMappingUtilsImpl();
    assertTrue(constraintViolationMappingUtilsImpl.extractErrorMessages(new HashSet<>()).isEmpty());
  }

  @Test
  void testExtractErrorMessage() {
    ConstraintViolationMappingUtilsImpl constraintViolationMappingUtilsImpl = new ConstraintViolationMappingUtilsImpl();
    ConstraintViolation<?> constraintViolation = (ConstraintViolation<?>) mock(
        ConstraintViolation.class);
    when(constraintViolation.getMessage()).thenReturn("hello world");
    when(constraintViolation.getPropertyPath()).thenReturn(PathImpl.createRootPath());
    assertEquals("hello world",
        constraintViolationMappingUtilsImpl.extractErrorMessage(constraintViolation));
    verify(constraintViolation).getPropertyPath();
    verify(constraintViolation).getMessage();
  }
}

