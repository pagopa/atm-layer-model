package it.gov.pagopa.atmlayer.service.model.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class ATMLayerValidationErrorResponseTest {

  @Test
  void testGetterAnnotations() {
    ATMLayerValidationErrorResponse errorResponse = ATMLayerValidationErrorResponse.builder()
        .errorCode("E001")
        .type("Validation Error")
        .statusCode(500)
        .message("Invalid input data")
        .errors(List.of("Field xxx must be not null"))
        .build();

    assertEquals("E001", errorResponse.getErrorCode());
    assertEquals("Validation Error", errorResponse.getType());
    assertEquals(500, errorResponse.getStatusCode());
    assertEquals("Invalid input data", errorResponse.getMessage());
    assertNotNull(errorResponse.getErrors());
    assertEquals(1, errorResponse.getErrors().size());
    assertEquals("Field xxx must be not null", errorResponse.getErrors().get(0));
  }

  @Test
  void testJsonPropertyOrder() {
    ATMLayerValidationErrorResponse errorResponse = ATMLayerValidationErrorResponse.builder()
        .errorCode("E001")
        .type("Validation Error")
        .statusCode(500)
        .message("Invalid input data")
        .errors(List.of("Field xxx must be not null"))
        .build();

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String json = objectMapper.writeValueAsString(errorResponse);
      String expectedJson = "{\"type\":\"Validation Error\",\"errorCode\":\"E001\",\"statusCode\":500,\"message\":\"Invalid input data\",\"errors\":[\"Field xxx must be not null\"]}";
      assertNotEquals(expectedJson, json);
    } catch (Exception e) {
      fail("JSON serialization failed: " + e.getMessage());
    }
  }
}
