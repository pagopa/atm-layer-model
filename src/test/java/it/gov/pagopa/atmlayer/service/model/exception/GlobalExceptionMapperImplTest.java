package it.gov.pagopa.atmlayer.service.model.exception;


import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.exception.mapper.GlobalExceptionMapperImpl;
import it.gov.pagopa.atmlayer.service.model.model.ATMLayerValidationErrorResponse;
import it.gov.pagopa.atmlayer.service.model.utils.ConstraintViolationMappingUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
@QuarkusTest
public class GlobalExceptionMapperImplTest {

    @Mock
    ConstraintViolationMappingUtils constraintViolationMappingUtils;

    @InjectMocks
    GlobalExceptionMapperImpl globalExceptionMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConstraintViolationExceptionMapper() {
        String message = "Message";
        HashSet<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException(message, constraintViolations);


        RestResponse<ATMLayerValidationErrorResponse> response = globalExceptionMapper.ConstraintViolationExceptionMapper(exception);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    }

    
}
