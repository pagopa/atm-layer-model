package it.gov.pagopa.atmlayer.service.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;

public class ConstraintViolationMappingUtilsImplTest {

  @Test
  void testExtractErrorMessages() {
    ConstraintViolationMappingUtilsImpl constraintViolationMappingUtilsImpl = new ConstraintViolationMappingUtilsImpl();
    assertTrue(constraintViolationMappingUtilsImpl.extractErrorMessages(new HashSet<>()).isEmpty());
  }

  @Test
  void testExtractErrorMessage_LeafNodeInIterable() {

    PathImpl path = mock(PathImpl.class);
    NodeImpl leafNode = mock(NodeImpl.class);
    NodeImpl parentNode = mock(NodeImpl.class);
    ConstraintViolation<?> error = mock(ConstraintViolation.class);

    when(error.getPropertyPath()).thenReturn(path);
    when(path.getLeafNode()).thenReturn(leafNode);
    when(leafNode.isInIterable()).thenReturn(true);
    when(leafNode.getParent()).thenReturn(parentNode);
    when(parentNode.asString()).thenReturn("fieldName");
    when(error.getMessage()).thenReturn("Error message");

    ConstraintViolationMappingUtilsImpl constraintViolationMappingUtils = new ConstraintViolationMappingUtilsImpl();
    String result = constraintViolationMappingUtils.extractErrorMessage(error);

    assertEquals("fieldName Error message", result);
  }


  @Test
  void testExtractErrorMessage_LeafNodeNotInIterable() {

    PathImpl path = mock(PathImpl.class);
    NodeImpl leafNode = mock(NodeImpl.class);
    ConstraintViolation<?> error = mock(ConstraintViolation.class);

    when(error.getPropertyPath()).thenReturn(path);
    when(path.getLeafNode()).thenReturn(leafNode);
    when(leafNode.isInIterable()).thenReturn(false);
    when(leafNode.asString()).thenReturn("fieldName");
    when(error.getMessage()).thenReturn("Another error message");

    ConstraintViolationMappingUtilsImpl constraintViolationMappingUtils = new ConstraintViolationMappingUtilsImpl();
    String result = constraintViolationMappingUtils.extractErrorMessage(error);

    assertEquals("fieldName Another error message", result);
  }
}

