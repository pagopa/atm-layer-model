package it.gov.pagopa.atml.mil.integration.model.dto;

import it.gov.pagopa.atml.mil.integration.model.Metadata;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import org.jboss.resteasy.reactive.PartType;

import java.io.File;

@Data
public class ModelDto {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_XML)
    private File file;

    @FormParam("metadata")
    @PartType(MediaType.APPLICATION_JSON)
    private Metadata metadata;
}
