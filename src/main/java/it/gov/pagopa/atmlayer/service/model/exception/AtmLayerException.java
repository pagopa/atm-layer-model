package it.gov.pagopa.atmlayer.service.model.exception;

import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import lombok.Builder;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Base exception for PDF receipts service exceptions
 */
@Getter
public class AtmLayerException extends ClientErrorException {

    @Schema(example = "Validation Error")
    private final String type;

    @Schema(example = "500")
    private final int statusCode;

    private String message;

    private String errorCode;

    @Builder
    public AtmLayerException(Response.Status statusCode, AppErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getErrorMessage(), statusCode);
        this.message = errorCodeEnum.getErrorMessage();
        this.type = errorCodeEnum.getType().name();
        this.statusCode = statusCode.getStatusCode();
        this.errorCode = errorCodeEnum.getErrorCode();
    }

    @Builder
    public AtmLayerException(String message, Response.Status statusCode, AppErrorCodeEnum errorCodeEnum) {
        super(message, statusCode);
        this.message = message;
        this.type = errorCodeEnum.getType().name();
        this.statusCode = statusCode.getStatusCode();
        this.errorCode = errorCodeEnum.getErrorCode();
    }

    @Builder
    public AtmLayerException(Response.Status statusCode, AppErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getErrorMessage(), statusCode, cause);
        this.message = errorCodeEnum.getErrorMessage();
        this.type = errorCodeEnum.getType().name();
        this.statusCode = statusCode.getStatusCode();
        this.errorCode = errorCodeEnum.getErrorCode();
    }

    @Builder
    public AtmLayerException(String message, Response.Status statusCode, AppErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(message, statusCode, cause);
        this.message = message;
        this.type = errorCodeEnum.getType().name();
        this.statusCode = statusCode.getStatusCode();
        this.errorCode = errorCodeEnum.getErrorCode();
    }

    @Builder
    public AtmLayerException(String message, Response.Status status, Throwable cause, String type) {
        super(message, status, cause);
        this.message = message;
        this.type = type;
        this.statusCode = status.getStatusCode();
    }

    @Builder
    public AtmLayerException(String message, Response.Status status, String type) {
        super(message, status);
        this.message = message;
        this.type = type;
        this.statusCode = status.getStatusCode();
    }


}