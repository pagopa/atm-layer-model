package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
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
    @Column(name = "resource_id", nullable = false, updatable = false)
    @Id
    private UUID resourceId;
    @Column(name = "sha256", unique = true)
    private String sha256;
    @Column(name = "resource_type")
    @Enumerated(EnumType.STRING)
    NoDeployableResourceType noDeployableResourceType;
    @OneToOne(mappedBy = "resourceEntity", cascade = CascadeType.ALL)
    ResourceFile resourceFile;
    @Column(name = "file_name")
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
    @Transient
    String storageKey;
    @Transient
    @Getter(AccessLevel.NONE)
    private String cdnUrl;
    @Column(name = "description")
    private String description;

    @PrePersist
    public void generateUUID() {
        if (getResourceId() == null) {
            setResourceId(UUID.randomUUID());
        }
    }

    public String getCdnUrl() {
        return ConfigProvider.getConfig().getValue("cdn.base-url", String.class)
                .concat("/").concat(StringUtils.substringAfter(this.resourceFile.getStorageKey(), ConfigProvider.getConfig().getValue("cdn.offset-path", String.class)));
    }

}
