package it.gov.pagopa.receipt.pdf.service.model;

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
  @Schema(example = "pagopa-receipt-pdf-service")
  private String name;

  @Schema(example = "1.2.3")
  private String version;

  @Schema(example = "dev")
  private String environment;

  @Schema(example = "Receipt PDF Service")
  private String description;
}
