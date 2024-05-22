package it.gov.pagopa.atmlayer.service.model.exception.mapper;

import io.quarkus.arc.properties.IfBuildProperty;
import io.smallrye.mutiny.CompositeException;
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

@IfBuildProperty(name = "mapper.enabled", stringValue = "true", enableIfMissing = true)
@Singleton
@Slf4j
public class GlobalExceptionMapperImpl {

    private final ConstraintViolationMappingUtils constraintViolationMappingUtils;

    @Inject
    public GlobalExceptionMapperImpl(ConstraintViolationMappingUtils constraintViolationMappingUtils){
        this.constraintViolationMappingUtils = constraintViolationMappingUtils;
    }

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionMapperImpl.class);

    @ServerExceptionMapper
    public RestResponse<ATMLayerValidationErrorResponse> constraintViolationExceptionMapper(ConstraintViolationException exception) {
        String message = "Validation Error on Payload";
        logger.error("Validation Error on Payload: ", exception);
        return buildErrorResponse(exception.getConstraintViolations(), message);
    }

    @ServerExceptionMapper
    public RestResponse<ATMLayerErrorResponse> genericExceptionMapper(AtmLayerException exception) {
        return buildErrorResponse(exception);
    }

    @ServerExceptionMapper
    public RestResponse<ATMLayerErrorResponse> compositeException(CompositeException exception) {
        return buildErrorResponse(new AtmLayerException(exception));
    }

    @ServerExceptionMapper
    public RestResponse<ATMLayerErrorResponse> genericExceptionMapper(Exception exception) {
        String message = "Generic Error";
        logger.error("Generic error found: ", exception);
        return buildErrorResponse(message);
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

    private RestResponse<ATMLayerErrorResponse> buildErrorResponse(String message) {
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, ATMLayerErrorResponse.builder()
                .type(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .message(message)
                .build());
    }

    private RestResponse<ATMLayerValidationErrorResponse> buildErrorResponse(Set<ConstraintViolation<?>> errors, String message) {
        List<String> errorMessages = constraintViolationMappingUtils.extractErrorMessages(errors);
        ATMLayerValidationErrorResponse payload = ATMLayerValidationErrorResponse.builder()
                .type(Response.Status.BAD_REQUEST.getReasonPhrase())
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .errors(errorMessages)
                .message(message)
                .build();
        return RestResponse.status(Response.Status.BAD_REQUEST, payload);
    }


}
