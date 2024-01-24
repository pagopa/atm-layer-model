package it.gov.pagopa.atmlayer.service.model.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BpmnIdDto {

    private UUID bpmnId;

    private Long modelVersion = 1L;
}
