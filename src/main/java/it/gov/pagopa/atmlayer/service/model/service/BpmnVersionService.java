package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BpmnVersionService {
    Uni<List<BpmnVersion>> getAll();

    Uni<List<BpmnVersion>> findByPKSet(Set<BpmnVersionPK> bpmnVersionPKSet);

    Uni<BpmnVersion> setBpmnVersionStatus(BpmnVersionPK key, StatusEnum status);

    Uni<BpmnVersion> save(BpmnVersion bpmnVersion);

    Uni<Boolean> delete(BpmnVersionPK bpmnVersionPK);

    Uni<Optional<BpmnVersion>> findBySHA256(String sha256);

    Uni<Optional<BpmnVersion>> findByDefinitionKey(String definitionKey);

    Uni<Optional<BpmnVersion>> findByPk(BpmnVersionPK bpmnVersionPK);

    Uni<List<BpmnBankConfig>> putAssociations(String acquirerId, String functionType, List<BpmnBankConfig> bpmnBankConfigs);

    Uni<BpmnVersion> deploy(BpmnVersionPK bpmnVersionPK);

    Uni<BpmnVersion> saveAndUpload(BpmnVersion bpmnVersion, File file, String filename);

    Uni<BpmnDTO> upgrade(BpmnUpgradeDto bpmnUpgradeDto);

    Uni<BpmnVersion> createBPMN(BpmnVersion bpmnVersion, File file, String filename);

    Uni<Void> disable(BpmnVersionPK bpmnVersionPK);

    Uni<PageInfo<BpmnVersion>> findBpmnFiltered(int pageIndex, int pageSize, String functionType, String modelVersion, String definitionVersionCamunda, String createdAt, String lastUpdatedAt,
                                                UUID bpmnId, UUID deploymentId, String camundaDefinitionId, String createdBy, String definitionKey, String deployedFileName,
                                                String lastUpdatedBy, String resource, String sha256, String status, String acquirerId, String branchId, String terminalId, String filename);
}
