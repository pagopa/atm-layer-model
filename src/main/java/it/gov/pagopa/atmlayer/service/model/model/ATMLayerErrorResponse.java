package it.gov.pagopa.atmlayer.service.model.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Model class for the error response
 */
@Getter
@Jacksonized
@JsonPropertyOrder({"type", "errorCode", "status", "message"})
@RegisterForReflection
@SuperBuilder
public class ATMLayerErrorResponse {

    private String errorCode;

    @Schema(example = "Validation Error")
    private String type;

    @Schema(example = "500")
    private int statusCode;

    private String message;
}