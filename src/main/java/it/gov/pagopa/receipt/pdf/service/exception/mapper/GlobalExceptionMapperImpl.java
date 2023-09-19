package it.gov.pagopa.receipt.pdf.service.exception.mapper;

import io.quarkus.arc.properties.IfBuildProperty;
import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.receipt.pdf.service.model.CustomErrorResponse;
import it.gov.pagopa.receipt.pdf.service.utils.ConstraintViolationMappingUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
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
public class GlobalExceptionMapperImpl {

    @Inject
    ConstraintViolationMappingUtils constraintViolationMappingUtils;

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionMapperImpl.class);

    @ServerExceptionMapper
    public RestResponse<CustomErrorResponse> ConstraintViolationExceptionMapper(ConstraintViolationException exception) {
        Response.Status status = BAD_REQUEST;
        String message = "Validation Error on Payload";
        logger.error("Validation Error on Payload: ", exception);
        return RestResponse.status(status, buildErrorResponse(exception.getConstraintViolations(), status, message));
    }

    @ServerExceptionMapper
    public RestResponse<CustomErrorResponse> genericExceptionMapper(Exception exception) {
        Response.Status status = INTERNAL_SERVER_ERROR;
        String message = "Generic Error";
        logger.error("Generic error found: ", exception);
        return RestResponse.status(status, buildErrorResponse(status, message));
    }

    private CustomErrorResponse buildErrorResponseWithErrorCode(AppErrorCodeEnum errorCode, Response.Status status, String message) {
        return CustomErrorResponse.builder()
                .type(status.getReasonPhrase())
                .status(status.getStatusCode())
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    private CustomErrorResponse buildErrorResponse(Response.Status status, String message) {
        return CustomErrorResponse.builder()
                .type(status.getReasonPhrase())
                .status(status.getStatusCode())
                .message(message)
                .build();
    }

    private CustomErrorResponse buildErrorResponse(Set<ConstraintViolation<?>> errors, Response.Status status, String message) {
        List<String> errorMessages = constraintViolationMappingUtils.extractErrorMessages(errors);
        return CustomErrorResponse.builder()
                .type(status.getReasonPhrase())
                .status(status.getStatusCode())
                .errors(errorMessages)
                .message(message)
                .build();
    }


}
