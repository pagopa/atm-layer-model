package it.gov.pagopa.atmlayer.service.model.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    @Schema(example = "atm-layer-model")
    private String name;

    @Schema(example = "1.2.3")
    private String version;

    @Schema(example = "dev")
    private String environment;

    @Schema(example = "ATM Layer - Model Service")
    private String description;
}
