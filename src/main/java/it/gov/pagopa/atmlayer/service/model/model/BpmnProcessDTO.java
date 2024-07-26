package it.gov.pagopa.atmlayer.service.model.model;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class BpmnProcessDTO {

    @Schema(format = "byte", maxLength = 255)
    private String camundaDefinitionId;
}
