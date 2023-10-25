package it.gov.pagopa.atml.mil.integration.model;

import io.vertx.ext.auth.impl.hash.SHA256;
import it.gov.pagopa.atml.mil.integration.enumeration.StatusEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

public class ModelEntity {

    private UUID bpmnId;

    private int modelVersion;

    private String deployedFileName;

    private String definitionKey;

    private String functionType;

    private StatusEnum status;

    private SHA256 sha256;

    private boolean enabled;

    private int definitionVersionCamunda;

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
