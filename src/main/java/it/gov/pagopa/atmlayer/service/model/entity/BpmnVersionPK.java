package it.gov.pagopa.atmlayer.service.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@ToString
@EqualsAndHashCode
@Builder
public class BpmnVersionPK implements Serializable {

    private UUID bpmnId;

    private Long modelVersion = 1L;
}
