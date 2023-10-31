package it.gov.pagopa.atmlayer.service.model.model.filestorage;

import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;

public class FormData {

    @RestForm("data")
    public File data;

    @RestForm
    @PartType(MediaType.TEXT_PLAIN)
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "deve essere della forma ${regexp} e non contenere l'estensione del file")
    public String filename;

    @RestForm
    @PartType(MediaType.TEXT_PLAIN)
    public String mimetype;

}