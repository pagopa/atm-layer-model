package it.gov.pagopa.atml.mil.integration.dto;

import it.gov.pagopa.atml.mil.integration.model.AssociationMetadata;
import jakarta.validation.Valid;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;

import java.io.File;

@Data
@NoArgsConstructor
public class BpmnAssociationDto {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_XML)
    private File file;

    @FormParam("metadata")
    @PartType(MediaType.APPLICATION_JSON)
    @Valid
    private AssociationMetadata associationMetadata;
}
