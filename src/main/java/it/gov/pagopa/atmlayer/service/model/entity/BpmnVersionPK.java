package it.gov.pagopa.atmlayer.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BpmnVersionPK implements Serializable {

    @Serial
    private static final long serialVersionUID = -6327455979830016850L;
    private UUID bpmnId;

    @Column(columnDefinition = "int default 1")
    private int modelVersion;
}
