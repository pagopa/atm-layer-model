package it.gov.pagopa.atml.mil.integration.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import it.gov.pagopa.atml.mil.integration.enumeration.AppErrorCodeEnum;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Model class for the error response
 */
@Getter
@Jacksonized
@JsonPropertyOrder({"type", "errorCode", "status", "message", "errors"})
@RegisterForReflection
@SuperBuilder
public class ATMLayerErrorResponse {

    private AppErrorCodeEnum errorCode;

    @Schema(example = "Validation Error")
    private String type;

    @Schema(example = "500")
    private int status;

    private String message;
}