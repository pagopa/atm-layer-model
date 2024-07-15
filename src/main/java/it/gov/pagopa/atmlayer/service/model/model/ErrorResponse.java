package it.gov.pagopa.atmlayer.service.model.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Model class for the error response
 */
@Getter
@Builder
@Jacksonized
@JsonPropertyOrder({"type", "title", "status", "detail", "instance"})
@RegisterForReflection
public class ErrorResponse {

  @Schema(format = "byte", maxLength = 255)
  private String type;

  @Schema(example = "Internal Server Error",format = "byte", maxLength = 255)
  private String title;

  @Schema(example = "500", minimum = "1", maximum = "999")
  private int status;

  @Schema(example = "An unexpected error has occurred. Please contact support.",format = "byte", maxLength = 1000)
  private String detail;

  @Schema(example = "ATMLM-500",format = "byte", maxLength = 255)
  private String instance;
}