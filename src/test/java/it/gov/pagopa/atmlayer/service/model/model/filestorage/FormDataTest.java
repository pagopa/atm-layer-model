package it.gov.pagopa.atmlayer.service.model.model.filestorage;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FormDataTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testFormDataValidation_Success() {
        FormData formData = new FormData();
        formData.data = new File("test.txt");
        formData.filename = "valid_filename";
        formData.mimetype = "text/plain";
        Set<ConstraintViolation<FormData>> violations = validator.validate(formData);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testFormDataValidation_InvalidFilename() {
        FormData formData = new FormData();
        formData.data = new File("test.txt");
        formData.filename = "invalid_filename!"; // Invalid filename with special character
        formData.mimetype = "text/plain";
        Set<ConstraintViolation<FormData>> violations = validator.validate(formData);
        assertEquals(1, violations.size());
        ConstraintViolation<FormData> violation = violations.iterator().next();
        assertEquals("deve essere della forma $^[a-zA-Z0-9_-]+$ e non contenere l'estensione del file", violation.getMessage());
    }

    @Test
    void testFormDataValidation_NullData() {
        FormData formData = new FormData();
        formData.data = null; // Null data
        formData.filename = "valid_filename";
        formData.mimetype = "text/plain";
        Set<ConstraintViolation<FormData>> violations = validator.validate(formData);
        assertTrue(violations.isEmpty());
    }
}
