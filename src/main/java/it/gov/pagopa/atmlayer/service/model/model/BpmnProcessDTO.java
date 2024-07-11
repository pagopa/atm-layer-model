package it.gov.pagopa.atmlayer.service.model.model;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class BpmnProcessDTO {

    @Size(max = 255)
    private String camundaDefinitionId;
}
