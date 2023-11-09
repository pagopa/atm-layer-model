package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bpmn_bank_config")
public class BpmnBankConfig extends PanacheEntityBase implements Serializable {

    @EmbeddedId
    private BpmnBankConfigPK bpmnBankConfigPK;

    @Column(name = "function_type")
    @Getter(AccessLevel.NONE)
    private String functionType;

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

    public String getFunctionType() {
        if (functionType != null) {
            return functionType.toUpperCase();
        }
        return null;
    }
}
