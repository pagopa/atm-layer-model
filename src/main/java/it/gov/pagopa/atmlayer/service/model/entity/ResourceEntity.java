package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "resource_entity")
public class ResourceEntity extends PanacheEntityBase implements Serializable {
    @Column(name = "bpmn_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID resourceId;
    //@OneToOne(mappedBy = "resourceEntity", cascade = CascadeType.ALL)
    //ResourceFile resourceFile;
    @Column(name = "sha256", unique = true)
    private String sha256;
    @Column(name="resourceType")
    ResourceTypeEnum resourceTypeEnum;
    @Column(name="file_name")
    String fileName;
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
