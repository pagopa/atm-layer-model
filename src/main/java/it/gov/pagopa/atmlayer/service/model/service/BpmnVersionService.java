package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BpmnVersionService {

    String decodeBase64(String s) throws IOException;

    Uni<List<BpmnVersion>> findByPKSet(Set<BpmnVersionPK> bpmnVersionPKSet);

    String calculateSHA256(File file) throws NoSuchAlgorithmException, IOException;

    Uni<BpmnVersion> save(BpmnVersion bpmnVersion);

    Uni<Boolean> delete(BpmnVersionPK bpmnVersionPK);

    Uni<Optional<BpmnVersion>> findBySHA256(String sha256);

    Uni<Optional<BpmnVersion>> findByPk(BpmnVersionPK bpmnVersionPK);

    Uni<List<BpmnBankConfig>> putAssociations(String acquirerId, FunctionTypeEnum functionTypeEnum, List<BpmnBankConfig> bpmnBankConfigs);

    Uni<BpmnVersion> setBpmnVersionStatus(UUID id, Long modelVersion, StatusEnum status);

    Uni<Boolean> checkBpmnFileExistence(UUID id, Long modelVersion);

    Uni<BpmnVersion> setDeployInfo(UUID id, Long modelVersion, DeployResponseDto response);

    Uni<BpmnVersion> saveAndUpload(BpmnVersion bpmnVersion, File file, String filename);


}
