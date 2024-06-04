package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedBPMNProcessDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedDMNDecisionDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.UtilityValues;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceFileRepository;
import it.gov.pagopa.atmlayer.service.model.repository.WorkflowResourceRepository;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceService;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceStorageService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.DEPLOYED_FILE_WAS_NOT_RETRIEVED;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.DEPLOY_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_CANNOT_BE_ROLLED_BACK;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_CANNOT_BE_UPDATED;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_FILE_WITH_SAME_CONTENT_ALREADY_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_NOT_DEPLOYED_CANNOT_ROLLBACK;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS;
import static it.gov.pagopa.atmlayer.service.model.utils.FileUtilities.calculateSha256;
import static it.gov.pagopa.atmlayer.service.model.utils.FileUtilities.extractIdValue;

@ApplicationScoped
@Slf4j
public class WorkflowResourceServiceImpl implements WorkflowResourceService {
    @Inject
    WorkflowResourceRepository workflowResourceRepository;
    @Inject
    ResourceFileRepository resourceFileRepository;
    @Inject
    WorkflowResourceStorageService workflowResourceStorageService;
    @Inject
    @RestClient
    ProcessClient processClient;

    @Override
    @WithTransaction
    public Uni<WorkflowResource> save(WorkflowResource workflowResource) {
        log.info("checking that no already existing file with sha256 {} exist", workflowResource.getSha256());
        return this.findBySHA256(workflowResource.getSha256())
                .onItem().transform(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException(String.format("Esiste già un file di risorsa aggiuntiva per processo con lo stesso contenuto: %s", x.get().getDeployedFileName()), Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_FILE_WITH_SAME_CONTENT_ALREADY_EXIST);
                    }
                    return x;
                }))
                .onItem().transformToUni(t -> {
                    log.info("Persisting Workflow Resource {} to database", workflowResource.getDeployedFileName());
                    return this.workflowResourceRepository.persist(workflowResource);
                });
    }

    @Override
    @WithSession
    public Uni<Optional<WorkflowResource>> findBySHA256(String sha256) {
        return this.workflowResourceRepository.findBySHA256(sha256)
                .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
    }

    @Override
    @WithSession
    public Uni<Optional<WorkflowResource>> findById(UUID id) {
        return workflowResourceRepository.findById(id).onItem().transformToUni(workflowResource -> Uni.createFrom().item(Optional.ofNullable(workflowResource)));
    }

    @Override
    @WithSession
    public Uni<Optional<WorkflowResource>> findByDefinitionKey(String definitionKey) {
        return this.workflowResourceRepository.findByDefinitionKey(definitionKey)
                .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
    }

    public Uni<WorkflowResource> checkWorkflowResourceExistence(UUID uuid) {
        return this.findById(uuid)
                .onItem().transform(
                        Unchecked.function(workflowResource -> {
                            if (workflowResource.isEmpty()) {
                                String errorMessage = String.format(
                                        "La risorsa aggiuntiva per processo a cui si fa riferimento con Id %s non esiste", uuid);
                                throw new AtmLayerException(errorMessage,Response.Status.BAD_REQUEST, WORKFLOW_FILE_DOES_NOT_EXIST);
                            }
                            return workflowResource.get();
                        }));
    }

    public Uni<Boolean> checkWorkflowResourceFileExistenceDeployable(UUID uuid) {
        return this.findById(uuid)
                .onItem()
                .transform(Unchecked.function(optionalWorkflowResource -> {
                            if (optionalWorkflowResource.isEmpty()) {
                                String errorMessage = String.format(
                                        "Una o alcune risorse aggiuntive per processo a cui si fa riferimento con Id %s non esiste", uuid);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        WORKFLOW_FILE_DOES_NOT_EXIST);
                            }
                            WorkflowResource workflowResource = optionalWorkflowResource.get();
                            return workflowResource.getStatus().equals(StatusEnum.CREATED) || workflowResource.getStatus()
                                    .equals(StatusEnum.DEPLOY_ERROR) || workflowResource.getStatus().equals(StatusEnum.UPDATED_BUT_NOT_DEPLOYED);
                        })
                );
    }

    public Uni<ResourceFile> checkResourceFileExistence(ResourceFile resourceFile, UUID workflowResourceId) {
        if (Objects.isNull(resourceFile) || StringUtils.isBlank(
                resourceFile.getStorageKey())) {
            String errorMessage = String.format(
                    "Nessun file associato alla risorsa aggiuntiva per processo o nessuna chiave di archiviazione trovata: %s", workflowResourceId);
            log.error(errorMessage);
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR,
                    AppErrorCodeEnum.WORKFLOW_RESOURCE_INTERNAL_ERROR);
        }
        return Uni.createFrom().item(resourceFile);
    }

    @WithTransaction
    public Uni<WorkflowResource> setWorkflowResourceStatus(UUID uuid, StatusEnum status) {
        return this.findById(uuid)
                .onItem()
                .transformToUni(Unchecked.function(optionalWorkflowResource -> {
                            if (optionalWorkflowResource.isEmpty()) {
                                String errorMessage = String.format(
                                        "Uno o alcuni dei file di risorse aggiuntive per processo a cui si fa riferimento non esistono: %s", uuid);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        WORKFLOW_FILE_DOES_NOT_EXIST);
                            }
                            WorkflowResource workflowToDeploy = optionalWorkflowResource.get();
                            workflowToDeploy.setStatus(status);
                            return this.workflowResourceRepository.persist(workflowToDeploy);
                        })
                );
    }

    @Override
    public Uni<WorkflowResource> deploy(UUID id, Optional<WorkflowResource> workflowResource) {
        return this.checkWorkflowResourceFileExistenceDeployable(id)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (Boolean.FALSE.equals(x)) {
                        String errorMessage = "Il file di risorsa aggiuntiva per processo a cui si fa riferimento non può essere rilasciato";
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                AppErrorCodeEnum.WORKFLOW_RESOURCE_FILE_CANNOT_BE_DEPLOYED);
                    }
                    if (workflowResource.isEmpty()) {
                        throw new AtmLayerException("Risorsa aggiuntiva per processo non trovata", Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    WorkflowResource resource = workflowResource.get();
                    DeployableResourceType resourceType = resource.getResourceType();
                    return this.setWorkflowResourceStatus(id, StatusEnum.WAITING_DEPLOY)
                            .onItem()
                            .transformToUni(workflowWaiting -> {
                                ResourceFile resourceFile = workflowWaiting.getResourceFile();
                                if (Objects.isNull(resourceFile) || StringUtils.isBlank(resourceFile.getStorageKey())) {
                                    String errorMessage = String.format("Nessun file associato alla risorsa aggiuntiva per processo o nessuna chiave di archiviazione trovata: %s", id);
                                    log.error(errorMessage);
                                    return Uni.createFrom().failure
                                            (new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.WORKFLOW_RESOURCE_CANNOT_BE_DELETED_FOR_STATUS));
                                }
                                return this.workflowResourceStorageService.generatePresignedUrl(resourceFile.getStorageKey())
                                        .onFailure()
                                        .recoverWithUni(failure -> {
                                            log.error(failure.getMessage());
                                            return this.setWorkflowResourceStatus(id, StatusEnum.DEPLOY_ERROR)
                                                    .onItem()
                                                    .transformToUni(y -> Uni.createFrom().failure(new AtmLayerException("Errore nel rilascio della risorsa aggiuntiva per processo. Impossibile generare presigned URL", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                                        });
                            })
                            .onItem()
                            .transformToUni(presignedUrl -> processClient.deploy(presignedUrl.toString(), resourceType.name())
                                    .onFailure()
                                    .recoverWithUni(failure -> {
                                        log.error(failure.getMessage());
                                        return this.setWorkflowResourceStatus(id, StatusEnum.DEPLOY_ERROR)
                                                .onItem()
                                                .transformToUni(y -> Uni.createFrom()
                                                        .failure(new AtmLayerException("Errore nel rilascio della risorsa aggiuntiva per processo. La comunicazione con Process Service non è riuscita", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                                    }))
                            .onItem()
                            .transformToUni(response -> this.setDeployInfo(id, response));
                }));
    }

    @WithTransaction
    public Uni<WorkflowResource> setDeployInfo(UUID uuid, DeployResponseDto response) {
        return this.findById(uuid)
                .onItem()
                .transformToUni(Unchecked.function(optionalWorkflowResource -> {
                    if (optionalWorkflowResource.isEmpty()) {
                        String errorMessage = String.format(
                                "Uno o alcuni dei file delle risorse aggiuntive per processo a cui si fa riferimento non esistono: %s", uuid);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    WorkflowResource workflowResource = optionalWorkflowResource.get();
                    if (response.getDeployedProcessDefinitions() != null) {
                        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = response.getDeployedProcessDefinitions();
                        Optional<DeployedBPMNProcessDefinitionDto> optionalDeployedProcessDefinition = deployedProcessDefinitions.values()
                                .stream().findFirst();
                        if (optionalDeployedProcessDefinition.isEmpty()) {
                            throw new AtmLayerException("Definizioni di processo vuote dal payload di rilascio", Response.Status.INTERNAL_SERVER_ERROR, DEPLOY_ERROR);
                        }
                        DeployedBPMNProcessDefinitionDto deployedProcessInfo = optionalDeployedProcessDefinition.get();
                        workflowResource.setDefinitionVersionCamunda(deployedProcessInfo.getVersion());
                        workflowResource.setDeployedFileName(deployedProcessInfo.getName());
                        workflowResource.setDescription(deployedProcessInfo.getDescription());
                        workflowResource.setResource(deployedProcessInfo.getResource());
                        workflowResource.setStatus(StatusEnum.DEPLOYED);
                        workflowResource.setCamundaDefinitionId(deployedProcessInfo.getId());
                    } else if (response.getDeployedDecisionDefinitions() != null) {
                        Map<String, DeployedDMNDecisionDefinitionDto> deployedDecisionDefinitions = response.getDeployedDecisionDefinitions();
                        Optional<DeployedDMNDecisionDefinitionDto> optionalDeployedDecisionDefinition = deployedDecisionDefinitions.values()
                                .stream().findFirst();
                        if (optionalDeployedDecisionDefinition.isEmpty()) {
                            throw new AtmLayerException("Definizioni decisionali vuote dal payload di rilascio", Response.Status.INTERNAL_SERVER_ERROR, DEPLOY_ERROR);
                        }
                        DeployedDMNDecisionDefinitionDto deployedDecisionDefinition = optionalDeployedDecisionDefinition.get();
                        workflowResource.setDefinitionVersionCamunda(deployedDecisionDefinition.getVersion());
                        workflowResource.setDeployedFileName(deployedDecisionDefinition.getName());
                        workflowResource.setResource(deployedDecisionDefinition.getResource());
                        workflowResource.setStatus(StatusEnum.DEPLOYED);
                        workflowResource.setCamundaDefinitionId(deployedDecisionDefinition.getId());
                    } else {
                        workflowResource.setDeployedFileName(response.getName());
                        workflowResource.setStatus(StatusEnum.DEPLOYED);
                    }
                    workflowResource.setDeploymentId(UUID.fromString(response.getId()));
                    return this.workflowResourceRepository.persist(workflowResource);
                }));
    }

    @Override
    @WithTransaction
    public Uni<WorkflowResource> saveAndUpload(WorkflowResource workflowResource, File file, String filename) {
        return this.save(workflowResource)
                .onItem().transformToUni(element -> this.workflowResourceStorageService.uploadFile(workflowResource, file, filename)
                        .onFailure().recoverWithUni(failure -> {
                            log.error(failure.getMessage());
                            return Uni.createFrom().failure(new AtmLayerException("Impossibile salvare la risorsa aggiuntiva per processo nell'Object Store. Creazione della risorsa aggiuntiva per processo interrotta", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                        })
                        .onItem().transformToUni(putObjectResponse -> {
                            log.info("Completed Workflow Resource Creation");
                            return Uni.createFrom().item(element);
                        }));
    }

    @Override
    public Uni<WorkflowResource> createWorkflowResource(WorkflowResource workflowResource, File file, String
            filename) {
        String definitionKey = extractIdValue(file, workflowResource.getResourceType());
        workflowResource.setDefinitionKey(definitionKey);
        return findByDefinitionKey(definitionKey)
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException("Esiste già una risorsa aggiuntiva per processo con la stessa chiave di definizione", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS);
                    }
                    return saveAndUpload(workflowResource, file, filename)
                            .onItem().transformToUni(workflow -> this.findById(workflow.getWorkflowResourceId())
                                    .onItem().transformToUni(optionalWorkflow -> {
                                        if (optionalWorkflow.isEmpty()) {
                                            return Uni.createFrom().failure(new AtmLayerException("Problema si sincronizzazione durante la creazione della risorsa aggiuntiva per processo", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
                                        }
                                        return Uni.createFrom().item(optionalWorkflow.get());
                                    }));
                }));
    }

    @Override
    public Uni<Void> disable(UUID uuid) {
        return this.setDisabledWorkflowResourceAttributes(uuid)
                        .onItem()
                        .transformToUni(disabledWorkflowResource -> Uni.createFrom().voidItem());
    }

    @WithTransaction
    public Uni<WorkflowResource> setDisabledWorkflowResourceAttributes(UUID uuid) {
        return this.checkWorkflowResourceExistence(uuid)
                .onItem().transformToUni(workflowResource -> {
                    workflowResource.setEnabled(false);
                    String disabledSha = workflowResource.getSha256().concat(UtilityValues.DISABLED_FLAG.getValue()).concat(workflowResource.getWorkflowResourceId().toString());
                    workflowResource.setSha256(disabledSha);
                    return this.workflowResourceRepository.persist(workflowResource);
                });
    }

    @WithTransaction
    @Override
    public Uni<Boolean> delete(UUID uuid) {
        log.info("Deleting Workflow Resource with id {}", uuid.toString());
        return this.findById(uuid)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (x.isEmpty()) {
                        throw new AtmLayerException(String.format("Risorsa aggiuntiva per processo con Id %s non esiste", uuid), Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    if (!StatusEnum.isEditable(x.get().getStatus())) {
                        throw new AtmLayerException(String.format("Risorsa aggiuntiva per processo con Id %s è in status %s e non può essere " +
                                "cancellata. Solamente i file delle risorse aggiuntive per processo con status %s possono essere cancellate", uuid, x.get().getStatus(), StatusEnum.getUpdatableAndDeletableStatuses()), Response.Status.BAD_REQUEST, AppErrorCodeEnum.WORKFLOW_RESOURCE_CANNOT_BE_DELETED_FOR_STATUS);
                    }
                    return Uni.createFrom().item(x.get());
                })).onItem().transformToUni(y -> this.workflowResourceRepository.deleteById(uuid));
    }

    @Override
    @WithSession
    public Uni<List<WorkflowResource>> getAll() {
        return this.workflowResourceRepository.findAll().list();
    }

    @Override
    @WithSession
    public Uni<PageInfo<WorkflowResource>> getAllFiltered(int pageIndex, int pageSize, StatusEnum status, UUID workflowResourceId, String deployedFileName, String definitionKey, DeployableResourceType resourceType, String sha256, String definitionVersionCamunda, String camundaDefinitionId, String description, String resource, UUID deploymentId, String fileName) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", status);
        filters.put("workflowResourceId", workflowResourceId);
        filters.put("deployedFileName", deployedFileName);
        filters.put("definitionKey", definitionKey);
        if (resourceType != null) filters.put("resourceType", resourceType.name());
        filters.put("sha256", sha256);
        filters.put("definitionVersionCamunda", definitionVersionCamunda);
        filters.put("camundaDefinitionId", camundaDefinitionId);
        filters.put("description", description);
        filters.put("resource", resource);
        filters.put("deploymentId", deploymentId);
        filters.put("fileName", fileName);
        filters.remove(null);
        filters.values().removeAll(Collections.singleton(null));
        filters.values().removeAll(Collections.singleton(""));
        return workflowResourceRepository.findByFilters(filters, pageIndex, pageSize);
    }

    @Override
    @WithTransaction
    public Uni<WorkflowResource> update(UUID id, File file, boolean isRollback) throws NoSuchAlgorithmException, IOException {
        return this.findById(id)
                .onItem()
                .transformToUni(Unchecked.function(workflow -> {
                    if (workflow.isEmpty()) {
                        throw new AtmLayerException(Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    WorkflowResource workflowFound = workflow.get();
                    log.info("Updating Workflow Resource with id {}", id.toString());
                    DeployableResourceType deployableResourceType = workflowFound.getResourceType();
                    String definitionKey = extractIdValue(file, deployableResourceType);
                    String storageKey = workflowFound.getResourceFile().getStorageKey();
                    log.info("storage key {}", storageKey);
                    String shaUpdateFile = calculateSha256(file);
                    if (workflowFound.getSha256().equals(shaUpdateFile)) {
                        throw new AtmLayerException("Il file caricato ha lo stesso contenuto della risorsa aggiuntiva per processo che si vuole aggiornare", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS);
                    }
                    workflowFound.setSha256(shaUpdateFile);
                    if (!workflowFound.getDefinitionKey().equals(definitionKey)) {
                        throw new AtmLayerException(String.format("La chiave di definizione nella tua risorsa aggiuntiva per processo: %s non corrisponde alla risorsa aggiuntiva per processo che stai tentando di aggiornare", definitionKey), Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_CANNOT_BE_UPDATED);
                    }
                    if (!isRollback && workflowFound.getStatus().equals(StatusEnum.DEPLOYED)) {
                        workflowFound.setStatus(StatusEnum.UPDATED_BUT_NOT_DEPLOYED);
                    }
                    if (isRollback && workflowFound.getStatus().equals(StatusEnum.UPDATED_BUT_NOT_DEPLOYED)) {
                        workflowFound.setStatus(StatusEnum.DEPLOYED);
                    }
                    Date date = new Date();
                    workflowFound.setLastUpdatedAt(new Timestamp(date.getTime()));
                    workflowFound.getResourceFile().setLastUpdatedAt(new Timestamp(date.getTime()));
                    return workflowResourceRepository.persist(workflowFound)
                            .onItem()
                            .transformToUni(x -> workflowResourceStorageService.updateFile(workflowFound, file))
                            .onItem().transformToUni(updatedFile -> this.findById(id)
                                    .onItem().transformToUni(optionalWorkflowResource -> Uni.createFrom().item(optionalWorkflowResource.get())));
                }));
    }

    @Override
    public Uni<WorkflowResource> rollback(UUID id) {
        return this.findById(id)
                .onItem()
                .transformToUni(Unchecked.function(workflow -> {
                    if (workflow.isEmpty()) {
                        throw new AtmLayerException("La risorsa aggiuntiva per processo a cui si fa riferimento non esiste", Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    WorkflowResource workflowResourceToRollBack = workflow.get();
                    if (workflowResourceToRollBack.getStatus().getValue().equals(StatusEnum.DEPLOYED.getValue())) {
                        throw new AtmLayerException("Impossibile ripristinare: la risorsa a cui si fa riferimento è l'ultima versione rilasciata", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_CANNOT_BE_ROLLED_BACK);
                    }
                    UUID deploymentId = workflowResourceToRollBack.getDeploymentId();
                    if (deploymentId == null) {
                        throw new AtmLayerException("CamundaDefinitionId della risorsa a cui si fa riferimento è NULL: impossibile ripristinare", Response.Status.NOT_FOUND, WORKFLOW_RESOURCE_NOT_DEPLOYED_CANNOT_ROLLBACK);
                    }
                    return processClient.getDeployedResource(deploymentId.toString())
                            .onFailure()
                            .recoverWithUni(exception ->
                                    Uni.createFrom().failure(new AtmLayerException("Errore durante il recupero della risorsa aggiuntiva per processo dal Process", Response.Status.INTERNAL_SERVER_ERROR, DEPLOYED_FILE_WAS_NOT_RETRIEVED)))
                            .onItem()
                            .transformToUni(Unchecked.function(file -> update(id, file, true)));
                }));
    }

    @Override
    public Multi<Buffer> download(UUID uuid) {
        return this.checkWorkflowResourceExistence(uuid)
                .onItem().transformToMulti(workflowResource -> {
                    ResourceFile resourceFile = workflowResource.getResourceFile();
                    return checkResourceFileExistence(resourceFile, uuid)
                            .onItem().transformToMulti(resourceFile1 -> this.workflowResourceStorageService.download(resourceFile1.getStorageKey()));
                });
    }

    @Override
    public Uni<String> downloadForFrontEnd(UUID uuid) {
        return this.checkWorkflowResourceExistence(uuid)
                .onItem().transformToUni(workflowResource -> {
                    ResourceFile resourceFile = workflowResource.getResourceFile();
                    return checkResourceFileExistence(resourceFile, uuid)
                            .onItem().transformToUni(resourceFile1 -> {
                                Context context = Vertx.currentContext();
                                return this.workflowResourceStorageService.download(resourceFile.getStorageKey())
                                        .collect().asList()
                                        .onItem().transform(buffers -> {
                                            Buffer totalBuffer = Buffer.buffer();
                                            for (Buffer buffer : buffers) {
                                                totalBuffer.appendBuffer(buffer);
                                            }
                                            return Base64.getEncoder().encodeToString(totalBuffer.getBytes());
                                        })
                                        .emitOn(command -> context.runOnContext(x -> command.run()));
                            });
                });
    }
}
