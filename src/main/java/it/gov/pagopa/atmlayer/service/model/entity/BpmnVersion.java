package it.gov.pagopa.atmlayer.service.model.entity;

import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToOne;
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
@IdClass(BpmnVersionPK.class)
public class BpmnVersion implements Serializable {

  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "bpmn_id", nullable = false, updatable = false)
  @Id
  private UUID bpmnId;

  @Column(name = "model_version", nullable = false)
  @Id
  private Long modelVersion = 1L;

  @OneToOne(mappedBy = "bpmn", cascade = CascadeType.ALL)
  ResourceFile resourceFile;

  @Column(name = "deployed_file_name")
  private String deployedFileName;

  @Column(name = "definition_key")
  private String definitionKey;

  @Column(name = "function_type")
  @Enumerated(EnumType.STRING)
  private FunctionTypeEnum functionType;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
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
