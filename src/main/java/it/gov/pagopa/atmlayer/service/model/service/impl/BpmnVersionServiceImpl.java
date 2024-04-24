package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.*;
import it.gov.pagopa.atmlayer.service.model.entity.*;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.UtilityValues;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnVersionMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
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
import java.util.*;
import java.util.regex.Pattern;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.*;
import static it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils.getSingleConfig;
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
    @WithSession
    public Uni<List<BpmnVersion>> getAll() {
        return this.bpmnVersionRepository.findAll().list();
    }

    @Override
    @WithSession
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
                        throw new AtmLayerException("Esiste già un file BPMN con lo stesso contenuto", Response.Status.BAD_REQUEST, BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST);
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
                        throw new AtmLayerException(String.format("BPMN con Id %s non esiste", bpmnVersionPK), Response.Status.NOT_FOUND, BPMN_FILE_DOES_NOT_EXIST);
                    }
                    if (!StatusEnum.isEditable(x.get().getStatus())) {
                        throw new AtmLayerException(String.format("BPMN con Id %s è nello stato %s e non può essere " +
                                "cancellato. È possibile eliminare solo i file BPMN con stato %s", bpmnVersionPK.toString(), x.get().getStatus(), StatusEnum.getUpdatableAndDeletableStatuses()), Response.Status.BAD_REQUEST, AppErrorCodeEnum.BPMN_CANNOT_BE_DELETED_FOR_STATUS);
                    }
                    return Uni.createFrom().item(x.get());
                })).onItem().transformToUni(y -> {
                    //todo fare la find per recuperare il deploymentId
                    processClient.undeploy(bpmnVersionPK.getBpmnId().toString());
                    return this.bpmnVersionRepository.deleteById(bpmnVersionPK);
                });
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
                                        "La chiave BPMN a cui si fa riferimento non esiste: %s", key);
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
                                        "La chiave BPMN a cui si fa riferimento non esiste: %s", bpmnVersionPK);
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

    @WithTransaction
    public Uni<BpmnVersion> setEnabledBpmnAttributes(BpmnVersionPK bpmnVersionPK) {
        return this.findByPk(bpmnVersionPK)
                .onItem()
                .transformToUni(Unchecked.function(optionalBpmn -> {
                            if (optionalBpmn.isEmpty()) {
                                String errorMessage = String.format(
                                        "La chiave BPMN a cui si fa riferimento non esiste: %s", bpmnVersionPK);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        BPMN_FILE_DOES_NOT_EXIST);
                            }
                            BpmnVersion bpmnVersion = optionalBpmn.get();
                            bpmnVersion.setEnabled(true);
                            String[] parts = bpmnVersion.getSha256().split(Pattern.quote(UtilityValues.DISABLED_FLAG.getValue()));
                            String enabledSha = parts[0];
                            bpmnVersion.setSha256(enabledSha);
                            return this.bpmnVersionRepository.persist(bpmnVersion);
                        })
                );
    }

    public Uni<BpmnVersion> checkBpmnFileExistence(BpmnVersionPK bpmnVersionPK) {
        return this.findByPk(bpmnVersionPK)
                .onItem()
                .transformToUni(Unchecked.function(optionalBpmn -> {
                    if (optionalBpmn.isEmpty()) {
                        String errorMessage = String.format(
                                "La chiave BPMN a cui si fa riferimento non esiste: %s", bpmnVersionPK);
                        return Uni.createFrom().failure(new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                BPMN_FILE_DOES_NOT_EXIST));
                    }
                    return Uni.createFrom().item(optionalBpmn.get());
                }));
    }

    public Uni<Boolean> checkBpmnFileExistenceDeployable(BpmnVersionPK bpmnVersionPK) {
        return this.findByPk(bpmnVersionPK)
                .onItem()
                .transform(Unchecked.function(optionalBpmn -> {
                            if (optionalBpmn.isEmpty()) {
                                String errorMessage = String.format(
                                        "Uno o alcuni dei file BPMN a cui si fa riferimento non esistono: %s", bpmnVersionPK);
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
                            return Uni.createFrom().failure(new AtmLayerException("Impossibile salvare BPMN nell'Object Store. Creazione BPMN interrotta", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
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
                        throw new AtmLayerException("Esiste già un BPMN con la stessa chiave di definizione", Response.Status.BAD_REQUEST, BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS);
                    }
                    return saveAndUpload(bpmnVersion, file, filename)
                            .onItem().transformToUni(bpmn -> this.findByPk(new BpmnVersionPK(bpmn.getBpmnId(), bpmn.getModelVersion()))
                                    .onItem().transformToUni(optionalBpmn -> {
                                        if (optionalBpmn.isEmpty()) {
                                            return Uni.createFrom().failure(new AtmLayerException("Problema di sincronizzazione durante la creazione del BPMN", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
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
                        throw new AtmLayerException(String.format("BPMN con Id %s non esiste", bpmnVersionPK), Response.Status.NOT_FOUND, BPMN_FILE_DOES_NOT_EXIST);
                    }
                    BpmnVersion bpmnVersion = referencedBpmn.get();
                    return bpmnBankConfigService.findByBpmnVersionPK(bpmnVersionPK)
                            .onItem()
                            .transformToUni(associations -> {
                                if (!associations.isEmpty()) {
                                    throw new AtmLayerException("Il BPMN di riferimento non può essere disabilitato perchè è associato", Response.Status.BAD_REQUEST, BPMN_CANNOT_BE_DISABLED_FOR_ASSOCIATIONS);
                                }
                                return setDisabledBpmnAttributes(bpmnVersionPK)
                                        .onItem()
                                        .transformToUni(disabledShaBpmn -> {
                                            if (StatusEnum.DEPLOYED.equals(bpmnVersion.getStatus())) {
                                                return processClient.undeploy(bpmnVersion.getDeploymentId().toString())
                                                        .onFailure()
                                                        .recoverWithUni(failure -> {
                                                            log.error(failure.getMessage());
                                                            return this.setEnabledBpmnAttributes(bpmnVersionPK)
                                                                    .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Errore nel undeploy del BPMN. Impossibile undeployare il BPMN", Response.Status.INTERNAL_SERVER_ERROR, BPMN_FILE_CANNOT_BE_UNDEPLOYED)));
                                                        });
                                            }
                                            return Uni.createFrom().voidItem();
                                        });
                            });
                });
    }

    @Override
    @WithSession
    public Uni<PageInfo<BpmnVersion>> findBpmnFiltered(int pageIndex, int pageSize, String functionType, String modelVersion, String definitionVersionCamunda,
                                                       UUID bpmnId, UUID deploymentId, String camundaDefinitionId, String definitionKey, String deployedFileName,
                                                       String resource, String sha256, StatusEnum status, String acquirerId, String branchId, String terminalId, String fileName) {
        if ((StringUtils.isEmpty(acquirerId) && (!StringUtils.isEmpty(branchId) || !StringUtils.isEmpty(terminalId))) || StringUtils.isEmpty(branchId) && !StringUtils.isEmpty(terminalId)) {
            return Uni.createFrom().failure(new AtmLayerException("AcquirerId deve essere specificato per BranchId, e BranchId deve essere specificato per TerminalId", Response.Status.BAD_REQUEST, ILLEGAL_CONFIGURATION_TRIPLET));
        }
        Map<String, Object> filters = new HashMap<>();
        filters.put("functionType", functionType);
        filters.put("modelVersion", modelVersion);
        filters.put("definitionVersionCamunda", definitionVersionCamunda);
        filters.put("bpmnId", bpmnId);
        filters.put("deploymentId", deploymentId);
        filters.put("camundaDefinitionId", camundaDefinitionId);
        filters.put("definitionKey", definitionKey);
        filters.put("deployedFileName", deployedFileName);
        filters.put("resource", resource);
        filters.put("sha256", sha256);
        filters.put("status", status);
        filters.put("acquirerId", acquirerId);
        filters.put("branchId", branchId);
        filters.put("terminalId", terminalId);
        filters.put("fileName", fileName);
        filters.remove(null);
        filters.values().removeAll(Collections.singleton(null));
        filters.values().removeAll(Collections.singleton(""));
        return bpmnVersionRepository.findByFilters(filters, pageIndex, pageSize);
    }

    @Override
    public Uni<BpmnBankConfig> addSingleAssociation(BpmnVersionPK bpmnVersionPK, BankConfigTripletDto bankConfigTripletDto) {
        return this.checkBpmnFileExistence(bpmnVersionPK)
                .onItem().transformToUni(bpmnVersion -> {
                    if (!bpmnVersion.getStatus().equals(StatusEnum.DEPLOYED)) {
                        String errorMessage = String.format("Il file di processo non è in stato DEPLOYED: %s", bpmnVersionPK);
                        return Uni.createFrom().failure(new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, BPMN_FILE_NOT_DEPLOYED));
                    }
                    return bpmnBankConfigService.findByConfigurationsAndFunction(bankConfigTripletDto.getAcquirerId(),
                                    bankConfigTripletDto.getBranchId(), bankConfigTripletDto.getTerminalId(), bpmnVersion.getFunctionType())
                            .onItem()
                            .transformToUni(optionalBankConfig -> {
                                if (optionalBankConfig.isPresent()) {
                                    String errorMessage = String.format("La banca/filiale/terminale indicata è già associata al processo con ID: %s , versione: %s", optionalBankConfig.get().getBpmnBankConfigPK().getBpmnId(), optionalBankConfig.get().getBpmnBankConfigPK().getBpmnModelVersion());
                                    throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.CONFIGURATION_TRIPLET_ALREADY_ASSOCIATED);
                                }
                                BpmnBankConfig bpmnBankConfig = getSingleConfig(bpmnVersionPK, bpmnVersion.getFunctionType(), bankConfigTripletDto);
                                return bpmnBankConfigService.save(bpmnBankConfig);
                            });
                });
    }

    @Override
    public Uni<Void> deleteSingleAssociation(BankConfigDeleteDto bankConfigDeleteDto) {
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK(bankConfigDeleteDto.getBpmnId(), bankConfigDeleteDto.getBpmnModelVersion(),
                bankConfigDeleteDto.getAcquirerId(), bankConfigDeleteDto.getBranchId(), bankConfigDeleteDto.getTerminalId());
        return bpmnBankConfigService.deleteByBankConfigPK(bpmnBankConfigPK)
                .onItem().transformToUni(deleted -> {
                    log.info(String.format("Deleted association: %s", bankConfigDeleteDto));
                    return Uni.createFrom().voidItem();
                });
    }

    @Override
    public Uni<BpmnBankConfig> replaceSingleAssociation(BpmnVersionPK bpmnVersionPK, BankConfigTripletDto bankConfigTripletDto) {
        return this.checkBpmnFileExistence(bpmnVersionPK)
                .onItem().transformToUni(bpmnVersion -> {
                    if (!bpmnVersion.getStatus().equals(StatusEnum.DEPLOYED)) {
                        String errorMessage = String.format("Il file di processo non è in stato DEPLOYED: %s", bpmnVersionPK);
                        return Uni.createFrom().failure(new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, BPMN_FILE_NOT_DEPLOYED));
                    }
                    return bpmnBankConfigService.findByConfigurationsAndFunction(bankConfigTripletDto.getAcquirerId(),
                                    bankConfigTripletDto.getBranchId(), bankConfigTripletDto.getTerminalId(), bpmnVersion.getFunctionType())
                            .onItem()
                            .transformToUni(optionalBankConfig -> {
                                if (optionalBankConfig.isEmpty()) {
                                    String errorMessage = String.format("AcquirerId:%s BranchId:%s TerminalId:%s non ha associazioni con tipo funzione %s. Crea invece una nuova associazione", bankConfigTripletDto.getAcquirerId(), bankConfigTripletDto.getBranchId(), bankConfigTripletDto.getTerminalId(), bpmnVersion.getFunctionType());
                                    return Uni.createFrom().failure(new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, CONFIGURATION_TRIPLET_NOT_ASSOCIATED));
                                }
                                BpmnBankConfig oldBpmnBankConfig = optionalBankConfig.get();
                                return bpmnBankConfigService.deleteByBankConfigPK(oldBpmnBankConfig.getBpmnBankConfigPK())
                                        .onItem().transformToUni(deleteSuccess -> {
                                            BpmnBankConfig bpmnBankConfig = getSingleConfig(bpmnVersionPK, bpmnVersion.getFunctionType(), bankConfigTripletDto);
                                            return bpmnBankConfigService.save(bpmnBankConfig);
                                        });
                            });
                });
    }

    public Uni<BpmnVersion> deploy(BpmnVersionPK bpmnVersionPK) {
        return this.checkBpmnFileExistenceDeployable(bpmnVersionPK)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (Boolean.FALSE.equals(x)) {
                        String errorMessage = "Il file BPMN di riferimento non può essere rilasciato";
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                AppErrorCodeEnum.BPMN_FILE_CANNOT_BE_DEPLOYED);
                    }
                    return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.WAITING_DEPLOY);
                }))
                .onItem()
                .transformToUni(bpmnWaiting -> {
                    ResourceFile resourceFile = bpmnWaiting.getResourceFile();
                    if (Objects.isNull(resourceFile) || StringUtils.isBlank(resourceFile.getStorageKey())) {
                        String errorMessage = String.format("Nessun file associato a BPMN o nessuna chiave di archiviazione trovata: %s", new BpmnVersionPK(bpmnWaiting.getBpmnId(), bpmnWaiting.getModelVersion()));
                        log.error(errorMessage);
                        return Uni.createFrom().failure
                                (new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.BPMN_FILE_CANNOT_BE_DEPLOYED));
                    }
                    return this.bpmnFileStorageService.generatePresignedUrl(resourceFile.getStorageKey())
                            .onFailure().recoverWithUni(failure -> {
                                log.error(failure.getMessage());
                                return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOY_ERROR)
                                        .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Errore nel rilascio del BPMN. Impossibile generare presigned URL", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                            });
                })
                .onItem().transformToUni(presignedUrl -> processClient.deploy(presignedUrl.toString(), DeployableResourceType.BPMN.name())
                        .onFailure().recoverWithUni(failure -> {
                            log.error(failure.getMessage());
                            return this.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOY_ERROR)
                                    .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Errore nel rilascio del BPMN. La comunicazione con Process Service non è riuscita", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                        })
                        .onItem()
                        .transformToUni(response -> this.setDeployInfo(bpmnVersionPK, response)));
    }

    public Uni<Void> undeploy(UUID uuid) {
        return checkBpmnFileExistenceUndeployable(uuid)
                .onItem()
                .transformToUni(exists -> {
                    if (!exists) {
                        String errorMessage = "Il file BPMN di riferimento non può essere undeployed";
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                BPMN_FILE_CANNOT_BE_UNDEPLOYED);
                    }
                    return processClient.undeploy(uuid.toString());
                });
    }

    private Uni<Boolean> checkBpmnFileExistenceUndeployable(UUID uuid) {
        //todo  Implementare la logica per controllare se il BPMN può essere undeployed
        return Uni.createFrom().item(Boolean.TRUE);
    }


    @WithTransaction
    public Uni<BpmnVersion> setDeployInfo(BpmnVersionPK key, DeployResponseDto response) {
        return this.findByPk(key)
                .onItem()
                .transformToUni(Unchecked.function(optionalBpmn -> {
                    if (optionalBpmn.isEmpty()) {
                        String errorMessage = String.format(
                                "Uno o alcuni dei file BPMN a cui si fa riferimento non esistono: %s", key);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                BPMN_FILE_DOES_NOT_EXIST);
                    }
                    BpmnVersion bpmnVersion = optionalBpmn.get();
                    Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = response.getDeployedProcessDefinitions();
                    Optional<DeployedBPMNProcessDefinitionDto> optionalDeployedProcessInfo = deployedProcessDefinitions.values()
                            .stream().findFirst();
                    if (optionalDeployedProcessInfo.isEmpty()) {
                        throw new AtmLayerException("Informazioni sul processo vuote dal payload di rilascio", Response.Status.INTERNAL_SERVER_ERROR, DEPLOY_ERROR);
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

    @WithSession
    public Uni<BpmnVersion> getLatestVersion(UUID uuid, String functionType) {
        return this.bpmnVersionRepository.findAllByIdAndFunction(uuid, functionType)
                .onItem()
                .transform(list -> list.get(0))
                .onFailure().recoverWithUni(failure -> {
                    log.error(failure.getMessage());
                    return Uni.createFrom().failure(new AtmLayerException(
                            "Non esiste alcun BPMN con l'Id e il tipo di funzione specificati",
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
                        String errorMessage = "Le chiavi di definizione differiscono, aggiornamento BPMN rifiutato";
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
                        throw new AtmLayerException("Errore generico calcolando SHA256", Response.Status.INTERNAL_SERVER_ERROR, SHA256_ERROR);
                    }
                })).onItem()
                .transformToUni(bpmnVersion -> saveAndUpload(bpmnVersion, bpmnUpgradeDto.getFile(),
                        bpmnUpgradeDto.getFilename()))
                .onItem()
                .transform(upgradedBpmn -> bpmnVersionMapper.toDTO(upgradedBpmn));
    }
}
