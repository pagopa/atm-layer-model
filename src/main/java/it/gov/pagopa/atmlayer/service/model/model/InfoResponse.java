package it.gov.pagopa.atmlayer.service.model.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Model class for the info response
 */
@Getter
@Builder
@Jacksonized
@JsonPropertyOrder({"name", "version", "environment", "description"})
public class InfoResponse {
    @Schema(example = "atm-layer-model", format = "byte", maxLength = 255)
    private String name;

    @Schema(example = "1.2.3", format = "byte", maxLength = 255)
    private String version;

    @Schema(example = "dev", format = "byte", maxLength = 255)
    private String environment;

    @Schema(example = "ATM Layer - Model Service", format = "byte", maxLength = 255)
    private String description;
}
