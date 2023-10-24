package it.gov.pagopa.atml.mil.integration.model;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import org.jboss.resteasy.reactive.PartType;

import java.io.File;
import java.io.InputStream;

@Data
public class ModelEntity {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_XML)
    public File file;
}
