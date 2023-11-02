package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class BpmnDTO {

    private UUID bpmnId;
    private Long modelVersion;
    private String deployedFileName;
    private String definitionKey;
    private FunctionTypeEnum functionType;
    private StatusEnum status;
    private String sha256;
    private Integer definitionVersionCamunda;
    private String camundaDefinitionId;
    private String description;
    private ResourceFileDTO resourceFile;
    private String resource;
    private UUID deploymentId;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private String createdBy;
    private String lastUpdatedBy;

}
