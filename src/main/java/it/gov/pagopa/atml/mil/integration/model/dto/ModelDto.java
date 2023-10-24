package it.gov.pagopa.atml.mil.integration.model.dto;

import it.gov.pagopa.atml.mil.integration.model.Metadata;
import jakarta.validation.Valid;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;

import java.io.File;

@Data
@NoArgsConstructor
public class ModelDto {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_XML)
    private File file;

    @FormParam("metadata")
    @PartType(MediaType.APPLICATION_JSON)
    @Valid
    private Metadata metadata;
}
