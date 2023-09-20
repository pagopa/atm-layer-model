package it.gov.pagopa.atml.mil.integration.exception.mapper;

import io.quarkus.arc.properties.IfBuildProperty;
import it.gov.pagopa.atml.mil.integration.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atml.mil.integration.utils.ConstraintViolationMappingUtils;
import it.gov.pagopa.atml.mil.integration.exception.AtmLayerRestException;
import it.gov.pagopa.atml.mil.integration.model.ATMLayerErrorResponse;
import it.gov.pagopa.atml.mil.integration.model.ATMLayerValidationErrorResponse;
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
    public RestResponse<ATMLayerErrorResponse> genericExceptionMapper(AtmLayerRestException exception) {
        if (exception.getStatusCode() == INTERNAL_SERVER_ERROR.getStatusCode()) {
            String message = "Generic Error";
            logger.error("Generic error found: ", exception);
            return buildErrorResponse(INTERNAL_SERVER_ERROR, message);
        }
        return buildErrorResponse(Response.Status.fromStatusCode(exception.getStatusCode()), exception.getMessage());
    }

    @ServerExceptionMapper
    public RestResponse<ATMLayerErrorResponse> genericExceptionMapper(Exception exception) {
        Response.Status status = INTERNAL_SERVER_ERROR;
        String message = "Generic Error";
        logger.error("Generic error found: ", exception);
        return buildErrorResponse(status, message);
    }

    private RestResponse<ATMLayerErrorResponse> buildErrorResponseWithErrorCode(AppErrorCodeEnum errorCode, Response.Status status, String message) {
        return RestResponse.status(status, ATMLayerErrorResponse.builder()
                .type(status.getReasonPhrase())
                .status(status.getStatusCode())
                .message(message)
                .errorCode(errorCode)
                .build());
    }

    private RestResponse<ATMLayerErrorResponse> buildErrorResponse(Response.Status status, String message) {
        return RestResponse.status(status, ATMLayerErrorResponse.builder()
                .type(status.getReasonPhrase())
                .status(status.getStatusCode())
                .message(message)
                .build());
    }

    private RestResponse<ATMLayerValidationErrorResponse> buildErrorResponse(Set<ConstraintViolation<?>> errors, Response.Status status, String message) {
        List<String> errorMessages = constraintViolationMappingUtils.extractErrorMessages(errors);
        ATMLayerValidationErrorResponse payload = ATMLayerValidationErrorResponse.builder()
                .type(status.getReasonPhrase())
                .status(status.getStatusCode())
                .errors(errorMessages)
                .message(message)
                .build();
        return RestResponse.status(status, payload);
    }


}
