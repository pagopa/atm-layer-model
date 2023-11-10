package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.WorkflowResourceTypeEnum;

import java.sql.Timestamp;
import java.util.UUID;

public class ResourceEntityDTO {
    private UUID resourceId;
    private String sha256;
    WorkflowResourceTypeEnum workflowResourceTypeEnum;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private String createdBy;
    private String lastUpdatedBy;
}
