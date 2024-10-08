package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalConfigs {
    @NotNull
    private UUID templateId;

    @NotNull
    @Schema(minimum = "1", maximum = "10000")
    private Long templateVersion;

   @Schema(type = SchemaType.ARRAY, maxItems = 10000)
    private List<String> terminalIds;
}
