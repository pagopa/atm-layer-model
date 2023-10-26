package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Setter
@NoArgsConstructor
public class BpmnVersion extends PanacheEntityBase {

    @EmbeddedId
    private BpmnVersionPK bpmnVersionPK;

    private String deployedFileName;

    private String definitionKey;

    private FunctionEnum functionType;

    private StatusEnum status;

    private String sha256;

    @Column(columnDefinition = "boolean default true")
    private Boolean enabled;

    private Integer definitionVersionCamunda;

    private String camundaDefinitionId;

    private String description;

    private String resource;

    private UUID deploymentId;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastUpdatedAt;

    private String createdBy;

    private String lastUpdatedBy;
}
