package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.enumeration.WorkflowResourceTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.FormParam;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
public class ResourceCreationDto {
    @FormParam("file")
    @NotNull(message = "resource file is required")
    private File file;
    @FormParam("filename")
    @NotNull(message = "filename  is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "deve essere della forma ${regexp} e non contenere l'estensione del file")
    private String filename;
    @FormParam("resourceType")
    @NotNull(message = "resource type is required")
    private WorkflowResourceTypeEnum resourceType;
}
