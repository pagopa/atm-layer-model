package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.model.CreationMetadata;
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
    private File file;

    @FormParam("metadata")
    @PartType(MediaType.APPLICATION_JSON)
    private CreationMetadata creationMetadata;
}
