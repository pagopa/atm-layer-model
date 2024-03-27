package it.gov.pagopa.atmlayer.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "resource_file_model")
public class ResourceFile extends PanacheEntityBase implements Serializable {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    @Id
    private UUID id;

    @Column(name = "resource_type")
    @Enumerated(EnumType.STRING)
    private S3ResourceTypeEnum resourceType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "bpmn_id", referencedColumnName = "bpmn_id"),
            @JoinColumn(name = "bpmn_model_version", referencedColumnName = "model_version")
    })
    @JsonIgnore
    private BpmnVersion bpmn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "workflow_resource_id", referencedColumnName = "workflow_resource_id")
    })
    @JsonIgnore
    private WorkflowResource workflowResource;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
    })
    @JsonIgnore
    private ResourceEntity resourceEntity;

    @NotNull
    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "file_name")
    private String fileName;

    private String extension;

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

    @PrePersist
    void onPrePersist() {
        this.extension = resourceType.getExtension();
    }
}
