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


    @Column(name = "bpmn_id")
    private UUID bpmnId;

    @Column(name = "model_version", columnDefinition = "int default 1")
    private int modelVersion;
}
