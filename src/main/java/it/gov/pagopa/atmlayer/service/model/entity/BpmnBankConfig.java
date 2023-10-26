package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BpmnBankConfig extends PanacheEntityBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 2192727048075093200L;
    @EmbeddedId
    private BpmnBankConfigPK bpmnBankConfigPK;

    @Column(name = "function_type")
    private FunctionEnum functionType;

    @Column(name = "enabled", columnDefinition = "boolean default true")
    private Boolean enabled;

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
