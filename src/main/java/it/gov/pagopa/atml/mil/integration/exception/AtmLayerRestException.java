package it.gov.pagopa.atml.mil.integration.exception;

import it.gov.pagopa.atml.mil.integration.enumeration.AppErrorCodeEnum;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import lombok.Builder;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

/**
 * Base exception for PDF receipts service exceptions
 */
@Getter
public class AtmLayerRestException extends ClientErrorException {

    private AppErrorCodeEnum errorCode;

    @Schema(example = "Validation Error")
    private final String type;

    @Schema(example = "500")
    private final int statusCode;

    private String message;

    @Schema(example = "An unexpected error has occurred. Please contact support.")
    private List<String> errors;

    @Builder
    public AtmLayerRestException(String message, Response.Status statusCode, String type, List<String> errors) {
        super(message, statusCode);
        this.message = message;
        this.type = type;
        this.errors = errors;
        this.statusCode = statusCode.getStatusCode();

    }
    
    public AtmLayerRestException(String message, Response.Status status, Throwable cause, String type, List<String> errors) {
        super(message, status, cause);
        this.message = message;
        this.type = type;
        this.errors = errors;
        this.statusCode = status.getStatusCode();
    }
}