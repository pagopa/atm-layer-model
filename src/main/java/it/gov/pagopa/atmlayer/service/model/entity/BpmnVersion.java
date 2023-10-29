package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import it.gov.pagopa.atmlayer.service.model.enumeration.functionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "bpmn_version")
public class BpmnVersion extends PanacheEntityBase implements Serializable {

    @EmbeddedId
    private BpmnVersionPK bpmnVersionPK;

    @Column(name = "deployed_file_name")
    private String deployedFileName;

    @Column(name = "definition_key")
    private String definitionKey;

    @Column(name = "function_type")
    @Enumerated(EnumType.STRING)
    private functionTypeEnum functionType;

    @Column(name = "status")
    private StatusEnum status;

    @Column(name = "sha256", unique = true)
    private String sha256;

    @Column(name = "enabled", columnDefinition = "boolean default true")
    private Boolean enabled;

    @Column(name = "definition_version_camunda")
    private Integer definitionVersionCamunda;

    @Column(name = "camunda_definition_id")
    private String camundaDefinitionId;

    @Column(name = "description")
    private String description;

    @Column(name = "resource")
    private String resource;

    @Column(name = "deployement_id")
    private UUID deploymentId;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
}
