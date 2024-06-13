package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

import java.io.File;
import java.util.List;

@Data
@NoArgsConstructor
public class ResourceMultipleCreationDto {
    @FormParam("file")
    @PartType("application/octet-stream")
    @NotNull(message = "resource file is required")
    private List<File> fileList;

    @FormParam("filename")
    @PartType(MediaType.APPLICATION_JSON)
    @NotNull(message = "filename  is required")
    private List<String> filenamList;

    @FormParam("resourceType")
    @PartType("text/plain")
    @NotNull(message = "resource type is required")
    private NoDeployableResourceType resourceType;

    @FormParam("path")
    @PartType("text/plain")
    @Pattern(regexp = "(^$)|(^(?!/)[a-zA-Z0-9/]+(?<!/)$)", message = "String must not start or end with '/' and must not contain white spaces and special characters")
    @DefaultValue("")
    @Schema(description = "Description of the path parameter: example/path",
            pattern = "(^$)|(^(?!/)[a-zA-Z0-9/]+(?<!/)$)")
    private String path;
    @FormParam("description")
    @PartType("text/plain")
    private String description;
}
