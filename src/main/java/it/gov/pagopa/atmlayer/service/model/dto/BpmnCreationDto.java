package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.enumeration.functionTypeEnum;
import jakarta.validation.constraints.NotNull;
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

    @FormParam("functionType")
    @NotNull(message = "function type is required")
    private functionTypeEnum functionType;
}
