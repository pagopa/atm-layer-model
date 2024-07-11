package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;

import java.io.File;

@Data
@NoArgsConstructor
public class BpmnCreationDto {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_XML)
    @NotNull(message = "bpmn file is required")
    private File file;

    @FormParam("filename")
    @NotNull(message = "field is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "deve essere della forma ${regexp} e non contenere l'estensione del file")
    @Size(max = 255)
    private String filename;

    @FormParam("functionType")
    @NotNull(message = "field is required")
    @Size(max = 255)
    private String functionType;

    @FormParam("description")
    @Nullable
    @Size(max = 255)
    private String description;
}
