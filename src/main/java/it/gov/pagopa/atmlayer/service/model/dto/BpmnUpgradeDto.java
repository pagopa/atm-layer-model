package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

import java.io.File;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BpmnUpgradeDto {
    @FormParam("uuid")
    @NotNull(message = "uuid is required")
    private UUID uuid;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_XML)
    @NotNull(message = "bpmn file is required")
    private File file;

    @FormParam("filename")
    @NotNull(message = "filename  is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "deve essere della forma ${regexp} e non contenere l'estensione del file")
    @Schema(format = "byte", maxLength = 255)
    private String filename;

    @FormParam("functionType")
    @NotNull(message = "function type is required")
    @Schema(format = "byte", maxLength = 255)
    private String functionType;

    @FormParam("description")
    @Nullable
    @Schema(format = "byte", maxLength = 255)
    private String description;
}
