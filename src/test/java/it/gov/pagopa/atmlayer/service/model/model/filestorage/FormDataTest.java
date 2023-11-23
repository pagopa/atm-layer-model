package it.gov.pagopa.atmlayer.service.model.model.filestorage;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormDataTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testFormDataValidation_Success() {
        // Arrange
        FormData formData = new FormData();
        formData.data = new File("test.txt");
        formData.filename = "valid_filename";
        formData.mimetype = "text/plain";

        // Act
        Set<ConstraintViolation<FormData>> violations = validator.validate(formData);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testFormDataValidation_InvalidFilename() {
        // Arrange
        FormData formData = new FormData();
        formData.data = new File("test.txt");
        formData.filename = "invalid_filename!"; // Invalid filename with special character
        formData.mimetype = "text/plain";

        // Act
        Set<ConstraintViolation<FormData>> violations = validator.validate(formData);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<FormData> violation = violations.iterator().next();
        assertEquals("deve essere della forma $^[a-zA-Z0-9_-]+$ e non contenere l'estensione del file", violation.getMessage());
    }

    @Test
    void testFormDataValidation_NullData() {
        // Arrange
        FormData formData = new FormData();
        formData.data = null; // Null data
        formData.filename = "valid_filename";
        formData.mimetype = "text/plain";

        // Act
        Set<ConstraintViolation<FormData>> violations = validator.validate(formData);

        // Assert
        assertTrue(violations.isEmpty());
    }

    // Add more tests for other scenarios as needed
}
