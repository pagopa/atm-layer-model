package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedProcessInfoDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
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

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;

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
                        throw new AtmLayerException(String.format("BPMN with id %s does not exists", bpmnVersionPK), Response.Status.NOT_FOUND, AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST);
                    }
                    if (!StatusEnum.isDeletable(x.get().getStatus())) {
                        throw new AtmLayerException(String.format("BPMN with id %s is in status %s and cannot be " +
                                "deleted. Only BPMN files in status %s can be deleted", bpmnVersionPK.toString(), x.get().getStatus(), StatusEnum.getDeletableStatuses()), Response.Status.BAD_REQUEST, AppErrorCodeEnum.BPMN_CANNOT_BE_DELETED_FOR_STATUS);
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
    public Uni<Optional<BpmnVersion>> findByPk(BpmnVersionPK bpmnVersionPK) {
        return bpmnVersionRepository.findById(bpmnVersionPK).onItem().transformToUni(bpmnVersion -> Uni.createFrom().item(Optional.ofNullable(bpmnVersion)));
    }

    @Override
    @WithTransaction
    public Uni<List<BpmnBankConfig>> putAssociations(String acquirerId, FunctionTypeEnum functionType, List<BpmnBankConfig> bpmnBankConfigs) {
        Uni<Long> deleteExistingUni = this.bpmnBankConfigService.deleteByAcquirerIdAndFunctionType(acquirerId, functionType);
        return deleteExistingUni
                .onItem()
                .transformToUni(x -> this.bpmnBankConfigService.saveList(bpmnBankConfigs))
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
                                        "One or some of the referenced BPMN files do not exists: %s", key);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST);
                            }
                            BpmnVersion bpmnToDeploy = optionalBpmn.get();
                            bpmnToDeploy.setStatus(status);
                            return this.bpmnVersionRepository.persist(bpmnToDeploy);
                        })
                );
    }

    private Uni<Boolean> checkBpmnFileExistence(BpmnVersionPK bpmnVersionPK) {
        return this.findByPk(bpmnVersionPK)
                .onItem()
                .transform(Unchecked.function(optionalBpmn -> {
                            if (optionalBpmn.isEmpty()) {
                                String errorMessage = String.format(
                                        "One or some of the referenced BPMN files do not exists: %s", bpmnVersionPK);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST);
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
                .onItem().transformToUni(record -> {
                    return this.bpmnFileStorageService.uploadFile(bpmnVersion, file, filename)
                            .onFailure().recoverWithUni(failure -> {
                                log.error(failure.getMessage());
                                return Uni.createFrom().failure(new AtmLayerException("Failed to save BPMN in Object Store. BPMN creation aborted", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                            })
                            .onItem().transformToUni(putObjectResponse -> {
                                log.info("Completed BPMN Creation");
                                return Uni.createFrom().item(record);
                            });
                });
    }

    public Uni<BpmnVersion> deploy(BpmnVersionPK bpmnVersionPK) {
        return this.checkBpmnFileExistence(bpmnVersionPK)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (!x) {
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
                                (new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.BPMN_CANNOT_BE_DELETED_FOR_STATUS));
                    }
                    return this.bpmnFileStorageService.generatePresignedUrl(resourceFile.getStorageKey())
                            .onFailure().recoverWithUni(failure -> {
                                log.error(failure.getMessage());
                                return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOY_ERROR)
                                        .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Error in BPMN deploy. Fail to generate presigned URL", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                            });
                })
                .onItem().transformToUni(presignedUrl -> {

                    return processClient.deploy(presignedUrl.toString())
                            .onFailure().recoverWithUni(failure -> {
                                log.error(failure.getMessage());
                                return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOY_ERROR)
                                        .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Error in BPMN deploy. Communication with Process Service failed", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                            })
                            .onItem()
                            .transformToUni(response -> this.setDeployInfo(bpmnVersionPK, response));
                });


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
                                AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST);
                    }
                    BpmnVersion bpmnVersion = optionalBpmn.get();
                    Map<String, DeployedProcessInfoDto> deployedProcessDefinitions = response.getDeployedProcessDefinitions();
                    Optional<DeployedProcessInfoDto> optionalDeployedProcessInfo = deployedProcessDefinitions.values()
                            .stream().findFirst();
                    if (optionalDeployedProcessInfo.isEmpty()) {
                        throw new RuntimeException("empty process info");
                    }
                    DeployedProcessInfoDto deployedProcessInfo = optionalDeployedProcessInfo.get();
                    bpmnVersion.setDefinitionVersionCamunda(deployedProcessInfo.getVersion());
                    bpmnVersion.setDeploymentId(deployedProcessInfo.getDeploymentId());
                    bpmnVersion.setCamundaDefinitionId(deployedProcessInfo.getId());
                    bpmnVersion.setDefinitionKey(deployedProcessInfo.getKey());
                    bpmnVersion.setDeployedFileName(deployedProcessInfo.getName());
                    bpmnVersion.setDescription(deployedProcessInfo.getDescription());
                    bpmnVersion.setResource(deployedProcessInfo.getResource());
                    bpmnVersion.setStatus(StatusEnum.DEPLOYED);
                    return this.bpmnVersionRepository.persist(bpmnVersion);
                }));
    }
}
