package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String filename;

    @FormParam("functionType")
    @NotNull(message = "function type is required")
    private String functionType;

    @FormParam("description")
    @Nullable
    private String description;
}
