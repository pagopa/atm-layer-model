package it.gov.pagopa.atmlayer.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class BpmnVersionPK implements Serializable {


    @Column(name = "bpmn_id")
    private UUID bpmnId;

    @Column(name = "model_version", columnDefinition = "int default 1")
    private int modelVersion;
}
