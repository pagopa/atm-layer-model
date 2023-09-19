package it.gov.pagopa.receipt.pdf.service.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

/**
 * Model class for the error response
 */
@Getter
@Builder
@Jacksonized
@JsonPropertyOrder({"type", "errorCode", "status", "message", "errors"})
@RegisterForReflection
public class CustomErrorResponse {

    private AppErrorCodeEnum errorCode;

    @Schema(example = "Validation Error")
    private String type;

    @Schema(example = "500")
    private int status;

    private String message;

    @Schema(example = "An unexpected error has occurred. Please contact support.")
    private List<String> errors;

}