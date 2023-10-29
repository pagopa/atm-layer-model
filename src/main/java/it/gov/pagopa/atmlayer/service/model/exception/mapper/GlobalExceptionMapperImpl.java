package it.gov.pagopa.atmlayer.service.model.exception.mapper;

import io.quarkus.arc.properties.IfBuildProperty;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.ATMLayerErrorResponse;
import it.gov.pagopa.atmlayer.service.model.model.ATMLayerValidationErrorResponse;
import it.gov.pagopa.atmlayer.service.model.utils.ConstraintViolationMappingUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@IfBuildProperty(name = "mapper.enabled", stringValue = "true", enableIfMissing = true)
@Singleton
@Slf4j
public class GlobalExceptionMapperImpl {

    @Inject
    ConstraintViolationMappingUtils constraintViolationMappingUtils;

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionMapperImpl.class);

    @ServerExceptionMapper
    public RestResponse<ATMLayerValidationErrorResponse> ConstraintViolationExceptionMapper(ConstraintViolationException exception) {
        String message = "Validation Error on Payload";
        logger.error("Validation Error on Payload: ", exception);
        return buildErrorResponse(exception.getConstraintViolations(), BAD_REQUEST, message);
    }

    @ServerExceptionMapper
    public RestResponse<ATMLayerErrorResponse> genericExceptionMapper(AtmLayerException exception) {
        if (exception.getStatusCode() == INTERNAL_SERVER_ERROR.getStatusCode()) {
            String message = "Generic Error";
            logger.error("Generic error found: ", exception);
            return buildErrorResponse(INTERNAL_SERVER_ERROR, message);
        }
        return buildErrorResponse(exception);
    }

    @ServerExceptionMapper
    public RestResponse<ATMLayerErrorResponse> genericExceptionMapper(Exception exception) {
        Response.Status status = INTERNAL_SERVER_ERROR;
        String message = "Generic Error";
        logger.error("Generic error found: ", exception);
        return buildErrorResponse(status, message);
    }

    private RestResponse<ATMLayerErrorResponse> buildErrorResponse(AtmLayerException e) {
        ATMLayerErrorResponse errorResponse = ATMLayerErrorResponse.builder()
                .type(e.getType())
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .errorCode(e.getErrorCode())
                .build();
        return RestResponse.status(Response.Status.fromStatusCode(e.getStatusCode()), errorResponse);
    }

    private RestResponse<ATMLayerErrorResponse> buildErrorResponse(Response.Status status, String message) {
        return RestResponse.status(status, ATMLayerErrorResponse.builder()
                .type(status.getReasonPhrase())
                .statusCode(status.getStatusCode())
                .message(message)
                .build());
    }

    private RestResponse<ATMLayerValidationErrorResponse> buildErrorResponse(Set<ConstraintViolation<?>> errors, Response.Status status, String message) {
        List<String> errorMessages = constraintViolationMappingUtils.extractErrorMessages(errors);
        ATMLayerValidationErrorResponse payload = ATMLayerValidationErrorResponse.builder()
                .type(status.getReasonPhrase())
                .statusCode(status.getStatusCode())
                .errors(errorMessages)
                .message(message)
                .build();
        return RestResponse.status(status, payload);
    }


}
