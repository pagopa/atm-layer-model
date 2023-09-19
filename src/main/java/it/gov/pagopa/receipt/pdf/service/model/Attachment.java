package it.gov.pagopa.receipt.pdf.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Builder
@Jacksonized
public class Attachment {

    @Schema(example = "id_allegato")
    private String id;

    @Schema(example = "application/pdf")
    @JsonProperty("content_type")
    private String contentType;

    @Schema(example = "Allegato 1.pdf")
    private String name;

    @Schema(example = "<percorso relativo dell'allegato>")
    private String url;

}
