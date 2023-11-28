package it.gov.pagopa.atmlayer.service.model.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class ErrorResponseTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    void testErrorResponseSerialization() throws Exception {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("error")
                .title("Internal Server Error")
                .status(500)
                .detail("An unexpected error has occurred. Please contact support.")
                .instance("ATMLM-500")
                .build();

        String json = objectMapper.writeValueAsString(errorResponse);
        ErrorResponse deserializedErrorResponse = objectMapper.readValue(json, ErrorResponse.class);
        assertNotEquals(errorResponse, deserializedErrorResponse);
    }

    @Test
    void testJsonPropertyOrder() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("error")
                .title("Internal Server Error")
                .status(500)
                .detail("An unexpected error has occurred. Please contact support.")
                .instance("ATMLM-500")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(errorResponse);
            String expectedJson = "{\"type\":\"error\",\"title\":\"Internal Server Error\",\"status\":500,\"detail\":\"An unexpected error has occurred. Please contact support.\",\"instance\":\"ATMLM-500\"}";
            assertEquals(expectedJson, json);
        } catch (Exception e) {
            fail("JSON serialization failed: " + e.getMessage());
        }
    }
}
