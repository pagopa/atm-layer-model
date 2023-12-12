package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedBPMNProcessDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.UtilityValues;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnVersionMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnVersionRepository;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_CANNOT_BE_DISABLED_FOR_ASSOCIATIONS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.DEPLOY_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.DUPLICATE_ASSOCIATION_CONFIGS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.SHA256_ERROR;
import static it.gov.pagopa.atmlayer.service.model.utils.FileUtilities.extractIdValue;

@ApplicationScoped
@Slf4j
public class BpmnVersionServiceImpl implements BpmnVersionService {
    @Inject
    BpmnVersionRepository bpmnVersionRepository;
    @Inject
    BpmnBankConfigService bpmnBankConfigService;
    @Inject
    BpmnFileStorageService bpmnFileStorageService;
    @Inject
    @RestClient
    ProcessClient processClient;
    @Inject
    BpmnVersionMapper bpmnVersionMapper;
    static final DeployableResourceType resourceType = DeployableResourceType.BPMN;

    @Override
    public Uni<List<BpmnVersion>> getAll() {
        return this.bpmnVersionRepository.findAll().list();
    }

    @Override
    public Uni<List<BpmnVersion>> findByPKSet(Set<BpmnVersionPK> bpmnVersionPKSet) {
        return this.bpmnVersionRepository.findByIds(bpmnVersionPKSet);
    }

    @Override
    @WithTransaction
    public Uni<BpmnVersion> save(BpmnVersion bpmnVersion) {
        log.info("checking that no already existing file with sha256 {} exist", bpmnVersion.getSha256());
        return this.findBySHA256(bpmnVersion.getSha256())
                .onItem().transform(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException("A BPMN file with the same content already exists", Response.Status.BAD_REQUEST, BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST);
                    }
                    return x;
                }))
                .onItem().transformToUni(t -> {
                    log.info("Persisting bpmn {} to database", bpmnVersion.getDeployedFileName());
                    return this.bpmnVersionRepository.persist(bpmnVersion);
                });
    }

    @WithTransaction
    @Override
    public Uni<Boolean> delete(BpmnVersionPK bpmnVersionPK) {
        log.info("Deleting BPMN with id {}", bpmnVersionPK.toString());
        return this.findByPk(bpmnVersionPK)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (x.isEmpty()) {
                        throw new AtmLayerException(String.format("BPMN with id %s does not exist", bpmnVersionPK), Response.Status.NOT_FOUND, BPMN_FILE_DOES_NOT_EXIST);
                    }
                    if (!StatusEnum.isEditable(x.get().getStatus())) {
                        throw new AtmLayerException(String.format("BPMN with id %s is in status %s and cannot be " +
                                "deleted. Only BPMN files in status %s can be deleted", bpmnVersionPK.toString(), x.get().getStatus(), StatusEnum.getUpdatableAndDeletableStatuses()), Response.Status.BAD_REQUEST, AppErrorCodeEnum.BPMN_CANNOT_BE_DELETED_FOR_STATUS);
                    }
                    return Uni.createFrom().item(x.get());
                })).onItem().transformToUni(y -> this.bpmnVersionRepository.deleteById(bpmnVersionPK));
    }

    @Override
    @WithSession
    public Uni<Optional<BpmnVersion>> findBySHA256(String sha256) {
        return this.bpmnVersionRepository.findBySHA256(sha256)
                .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
    }

    @Override
    @WithSession
    public Uni<Optional<BpmnVersion>> findByDefinitionKey(String definitionKey) {
        return this.bpmnVersionRepository.findByDefinitionKey(definitionKey)
                .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
    }

    @Override
    @WithSession
    public Uni<Optional<BpmnVersion>> findByPk(BpmnVersionPK bpmnVersionPK) {
        return bpmnVersionRepository.findById(bpmnVersionPK).onItem().transformToUni(bpmnVersion -> Uni.createFrom().item(Optional.ofNullable(bpmnVersion)));
    }

    @Override
    @WithTransaction
    public Uni<List<BpmnBankConfig>> putAssociations(String acquirerId, String functionType, List<BpmnBankConfig> bpmnBankConfigs) {
        Uni<Long> deleteExistingUni = this.bpmnBankConfigService.deleteByAcquirerIdAndFunctionType(acquirerId, functionType);
        return deleteExistingUni
                .onItem()
                .transformToUni(x -> this.bpmnBankConfigService.saveList(bpmnBankConfigs)
                        .onFailure()
                        .recoverWithUni(Uni.createFrom().failure(new AtmLayerException(Response.Status.BAD_REQUEST,DUPLICATE_ASSOCIATION_CONFIGS))))
                .onItem()
                .transformToUni(y -> this.bpmnBankConfigService.findByAcquirerIdAndFunctionType(acquirerId, functionType));
    }

    @WithTransaction
    public Uni<BpmnVersion> setBpmnVersionStatus(BpmnVersionPK key, StatusEnum status) {
        return this.findByPk(key)
                .onItem()
                .transformToUni(Unchecked.function(optionalBpmn -> {
                            if (optionalBpmn.isEmpty()) {
                                String errorMessage = String.format(
                                        "The referenced BPMN key does not exist: %s", key);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        BPMN_FILE_DOES_NOT_EXIST);
                            }
                            BpmnVersion bpmnToDeploy = optionalBpmn.get();
                            bpmnToDeploy.setStatus(status);
                            return this.bpmnVersionRepository.persist(bpmnToDeploy);
                        })
                );
    }

    @WithTransaction
    public Uni<BpmnVersion> setDisabledBpmnAttributes(BpmnVersionPK bpmnVersionPK) {
        return this.findByPk(bpmnVersionPK)
                .onItem()
                .transformToUni(Unchecked.function(optionalBpmn -> {
                            if (optionalBpmn.isEmpty()) {
                                String errorMessage = String.format(
                                        "The referenced BPMN key does not exist: %s", bpmnVersionPK);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        BPMN_FILE_DOES_NOT_EXIST);
                            }
                            BpmnVersion bpmnVersion = optionalBpmn.get();
                            bpmnVersion.setEnabled(false);
                            String disabledSha = bpmnVersion.getSha256().concat(UtilityValues.DISABLED_FLAG.getValue()).concat(bpmnVersion.getBpmnId().toString());
                            bpmnVersion.setSha256(disabledSha);
                            return this.bpmnVersionRepository.persist(bpmnVersion);
                        })
                );
    }

    public Uni<Boolean> checkBpmnFileExistence(BpmnVersionPK bpmnVersionPK) {
        return this.findByPk(bpmnVersionPK)
                .onItem()
                .transform(Unchecked.function(optionalBpmn -> {
                            if (optionalBpmn.isEmpty()) {
                                String errorMessage = String.format(
                                        "One or some of the referenced BPMN files do not exists: %s", bpmnVersionPK);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        BPMN_FILE_DOES_NOT_EXIST);
                            }
                            BpmnVersion bpmnVersion = optionalBpmn.get();
                            return bpmnVersion.getStatus().equals(StatusEnum.CREATED) || bpmnVersion.getStatus()
                                    .equals(StatusEnum.DEPLOY_ERROR);
                        })
                );
    }

    @Override
    @WithTransaction
    public Uni<BpmnVersion> saveAndUpload(BpmnVersion bpmnVersion, File file, String filename) {
        return this.save(bpmnVersion)
                .onItem().transformToUni(element -> this.bpmnFileStorageService.uploadFile(bpmnVersion, file, filename)
                        .onFailure().recoverWithUni(failure -> {
                            log.error(failure.getMessage());
                            return Uni.createFrom().failure(new AtmLayerException("Failed to save BPMN in Object Store. BPMN creation aborted", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                        })
                        .onItem().transformToUni(resourceFile -> {
                            element.setResourceFile(resourceFile);
                            log.info("Completed BPMN Creation");
                            return Uni.createFrom().item(element);
                        }));
    }

    @Override
    public Uni<BpmnVersion> createBPMN(BpmnVersion bpmnVersion, File file, String filename) {
        String definitionKey = extractIdValue(file, resourceType);
        bpmnVersion.setDefinitionKey(definitionKey);
        return findByDefinitionKey(definitionKey)
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException("A BPMN with the same definitionKey already exists", Response.Status.BAD_REQUEST, BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS);
                    }
                    return saveAndUpload(bpmnVersion, file, filename)
                            .onItem().transformToUni(bpmn -> this.findByPk(new BpmnVersionPK(bpmn.getBpmnId(), bpmn.getModelVersion()))
                                    .onItem().transformToUni(optionalBpmn -> {
                                        if (optionalBpmn.isEmpty()) {
                                            return Uni.createFrom().failure(new AtmLayerException("Sync problem on bpmn creation", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
                                        }
                                        return Uni.createFrom().item(optionalBpmn.get());
                                    }));
                }));
    }

    @Override
    public Uni<Void> disable(BpmnVersionPK bpmnVersionPK) {
        return findByPk(bpmnVersionPK)
                .onItem()
                .transformToUni(referencedBpmn -> {
                    if (referencedBpmn.isEmpty()) {
                        throw new AtmLayerException(String.format("BPMN with id %s does not exist", bpmnVersionPK), Response.Status.NOT_FOUND, BPMN_FILE_DOES_NOT_EXIST);
                    }
                    return bpmnBankConfigService.findByBpmnVersionPK(bpmnVersionPK)
                            .onItem()
                            .transformToUni(associations -> {
                                if (!associations.isEmpty()) {
                                    throw new AtmLayerException("The referenced BPMN cannot be disabled because it is associated", Response.Status.BAD_REQUEST, BPMN_CANNOT_BE_DISABLED_FOR_ASSOCIATIONS);
                                }
                                return setDisabledBpmnAttributes(bpmnVersionPK)
                                        .onItem()
                                        .transformToUni(disabledShaBpmn -> Uni.createFrom().voidItem());
                            });
                });
    }

    public Uni<BpmnVersion> deploy(BpmnVersionPK bpmnVersionPK) {
        return this.checkBpmnFileExistence(bpmnVersionPK)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (Boolean.FALSE.equals(x)) {
                        String errorMessage = "The referenced BPMN file can not be deployed";
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                AppErrorCodeEnum.BPMN_FILE_CANNOT_BE_DEPLOYED);
                    }
                    return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.WAITING_DEPLOY);
                }))
                .onItem()
                .transformToUni(bpmnWaiting -> {
                    ResourceFile resourceFile = bpmnWaiting.getResourceFile();
                    if (Objects.isNull(resourceFile) || StringUtils.isBlank(resourceFile.getStorageKey())) {
                        String errorMessage = String.format("No file associated to BPMN or no storage key found: %s", new BpmnVersionPK(bpmnWaiting.getBpmnId(), bpmnWaiting.getModelVersion()));
                        log.error(errorMessage);
                        return Uni.createFrom().failure
                                (new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.BPMN_FILE_CANNOT_BE_DEPLOYED));
                    }
                    return this.bpmnFileStorageService.generatePresignedUrl(resourceFile.getStorageKey())
                            .onFailure().recoverWithUni(failure -> {
                                log.error(failure.getMessage());
                                return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOY_ERROR)
                                        .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Error in BPMN deploy. Fail to generate presigned URL", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                            });
                })
                .onItem().transformToUni(presignedUrl -> processClient.deploy(presignedUrl.toString(), DeployableResourceType.BPMN.name())
                        .onFailure().recoverWithUni(failure -> {
                            log.error(failure.getMessage());
                            return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOY_ERROR)
                                    .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Error in BPMN deploy. Communication with Process Service failed", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                        })
                        .onItem()
                        .transformToUni(response -> this.setDeployInfo(bpmnVersionPK, response)));
    }

    @WithTransaction
    public Uni<BpmnVersion> setDeployInfo(BpmnVersionPK key, DeployResponseDto response) {
        return this.findByPk(key)
                .onItem()
                .transformToUni(Unchecked.function(optionalBpmn -> {
                    if (optionalBpmn.isEmpty()) {
                        String errorMessage = String.format(
                                "One or some of the referenced BPMN files do not exists: %s", key);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                BPMN_FILE_DOES_NOT_EXIST);
                    }
                    BpmnVersion bpmnVersion = optionalBpmn.get();
                    Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = response.getDeployedProcessDefinitions();
                    Optional<DeployedBPMNProcessDefinitionDto> optionalDeployedProcessInfo = deployedProcessDefinitions.values()
                            .stream().findFirst();
                    if (optionalDeployedProcessInfo.isEmpty()) {
                        throw new AtmLayerException("Empty process infos from deploy payload", Response.Status.INTERNAL_SERVER_ERROR, DEPLOY_ERROR);
                    }
                    DeployedBPMNProcessDefinitionDto deployedProcessInfo = optionalDeployedProcessInfo.get();
                    bpmnVersion.setDefinitionVersionCamunda(deployedProcessInfo.getVersion());
                    bpmnVersion.setDeploymentId(UUID.fromString(response.getId()));
                    bpmnVersion.setCamundaDefinitionId(deployedProcessInfo.getId());
                    bpmnVersion.setDeployedFileName(deployedProcessInfo.getName());
                    bpmnVersion.setDescription(deployedProcessInfo.getDescription());
                    bpmnVersion.setResource(deployedProcessInfo.getResource());
                    bpmnVersion.setStatus(StatusEnum.DEPLOYED);
                    return this.bpmnVersionRepository.persist(bpmnVersion);
                }));
    }

    public Uni<BpmnVersion> getLatestVersion(UUID uuid, String functionType) {
        return this.bpmnVersionRepository.findByIdAndFunction(uuid, functionType)
                .onItem()
                .transform(list -> list.get(0))
                .onFailure().recoverWithUni(failure -> {
                    log.error(failure.getMessage());
                    return Uni.createFrom().failure(new AtmLayerException(
                            "NO BPMN with given ID and functionType exists",
                            Response.Status.BAD_REQUEST, BPMN_FILE_DOES_NOT_EXIST));
                });
    }

    @Override
    public Uni<BpmnDTO> upgrade(BpmnUpgradeDto bpmnUpgradeDto) {
        String definitionKey = extractIdValue(bpmnUpgradeDto.getFile(), resourceType);
        return this.getLatestVersion(bpmnUpgradeDto.getUuid(), bpmnUpgradeDto.getFunctionType())
                .onItem()
                .transform(Unchecked.function(latestBPMN -> {
                    if (!extractIdValue(bpmnUpgradeDto.getFile(), resourceType).equals(latestBPMN.getDefinitionKey())) {
                        String errorMessage = "Definition keys differ, BPMN upgrade refused";
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                AppErrorCodeEnum.BPMN_FILE_CANNOT_BE_UPGRADED);
                    }
                    return latestBPMN;
                }))
                .onItem()
                .transform(BpmnVersion::getModelVersion).onItem()
                .transform(Unchecked.function(latestVersion -> {
                    try {
                        return bpmnVersionMapper.toEntityUpgrade(bpmnUpgradeDto, latestVersion + 1, definitionKey);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new AtmLayerException("Generic error calculating SHA256", Response.Status.INTERNAL_SERVER_ERROR, SHA256_ERROR);
                    }
                })).onItem()
                .transformToUni(bpmnVersion -> saveAndUpload(bpmnVersion, bpmnUpgradeDto.getFile(),
                        bpmnUpgradeDto.getFilename()))
                .onItem()
                .transform(upgradedBpmn -> bpmnVersionMapper.toDTO(upgradedBpmn));
    }
}
