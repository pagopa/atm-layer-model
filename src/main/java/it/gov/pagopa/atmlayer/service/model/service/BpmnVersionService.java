package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BpmnVersionService {


    Uni<List<BpmnVersion>> findByPKSet(Set<BpmnVersionPK> bpmnVersionPKSet);

    Uni<BpmnVersion> setBpmnVersionStatus(BpmnVersionPK key, StatusEnum status);
    Uni<BpmnVersion> save(BpmnVersion bpmnVersion);

    Uni<Boolean> delete(BpmnVersionPK bpmnVersionPK);

    Uni<Optional<BpmnVersion>> findBySHA256(String sha256);

    Uni<Optional<BpmnVersion>> findByPk(BpmnVersionPK bpmnVersionPK);

    Uni<List<BpmnBankConfig>> putAssociations(String acquirerId, FunctionTypeEnum functionTypeEnum, List<BpmnBankConfig> bpmnBankConfigs);

    Uni<BpmnVersion> deploy(BpmnVersionPK bpmnVersionPK);

    Uni<BpmnVersion> saveAndUpload(BpmnVersion bpmnVersion, File file, String filename);

    Uni<BpmnDTO> upgrade(BpmnUpgradeDto bpmnUpgradeDto);


}
