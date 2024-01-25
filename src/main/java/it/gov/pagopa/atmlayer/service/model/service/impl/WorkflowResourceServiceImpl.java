package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedBPMNProcessDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedDMNDecisionDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceFileRepository;
import it.gov.pagopa.atmlayer.service.model.repository.WorkflowResourceRepository;
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
import java.util.*;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.*;
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
                        throw new AtmLayerException("A Workflow Resource file with the same content already exists", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_FILE_WITH_SAME_CONTENT_ALREADY_EXIST);
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

    public Uni<Boolean> checkWorkflowResourceFileExistence(UUID uuid) {
        return this.findById(uuid)
                .onItem()
                .transform(Unchecked.function(optionalWorkflowResource -> {
                            if (optionalWorkflowResource.isEmpty()) {
                                String errorMessage = String.format(
                                        "One or some of the referenced Workflow Resource files with id: %s do not exists", uuid);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        WORKFLOW_FILE_DOES_NOT_EXIST);
                            }
                            WorkflowResource workflowResource = optionalWorkflowResource.get();
                            return workflowResource.getStatus().equals(StatusEnum.CREATED) || workflowResource.getStatus()
                                    .equals(StatusEnum.DEPLOY_ERROR) || workflowResource.getStatus().equals(StatusEnum.UPDATED_BUT_NOT_DEPLOYED);
                        })
                );
    }

    @WithTransaction
    public Uni<WorkflowResource> setWorkflowResourceStatus(UUID uuid, StatusEnum status) {
        return this.findById(uuid)
                .onItem()
                .transformToUni(Unchecked.function(optionalWorkflowResource -> {
                            if (optionalWorkflowResource.isEmpty()) {
                                String errorMessage = String.format(
                                        "One or some of the referenced Workflow Resource files do not exists: %s", uuid);
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
        return this.checkWorkflowResourceFileExistence(id)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (Boolean.FALSE.equals(x)) {
                        String errorMessage = "The referenced Workflow Resource file can not be deployed";
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                AppErrorCodeEnum.WORKFLOW_RESOURCE_FILE_CANNOT_BE_DEPLOYED);
                    }
                    if (workflowResource.isEmpty()) {
                        throw new AtmLayerException("Workflow Resource not found", Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    WorkflowResource resource = workflowResource.get();
                    DeployableResourceType resourceType = resource.getResourceType();
                    return this.setWorkflowResourceStatus(id, StatusEnum.WAITING_DEPLOY)
                            .onItem()
                            .transformToUni(workflowWaiting -> {
                                ResourceFile resourceFile = workflowWaiting.getResourceFile();
                                if (Objects.isNull(resourceFile) || StringUtils.isBlank(resourceFile.getStorageKey())) {
                                    String errorMessage = String.format("No file associated to Workflow Resource or no storage key found: %s", id);
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
                                                    .transformToUni(y -> Uni.createFrom().failure(new AtmLayerException("Error in Workflow Resource deploy. Fail to generate presigned URL", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
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
                                                        .failure(new AtmLayerException("Error in Workflow Resource deploy. Communication with Process Service failed", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
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
                                "One or some of the referenced Workflow Resource files do not exists: %s", uuid);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    WorkflowResource workflowResource = optionalWorkflowResource.get();
                    if (response.getDeployedProcessDefinitions() != null) {
                        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = response.getDeployedProcessDefinitions();
                        Optional<DeployedBPMNProcessDefinitionDto> optionalDeployedProcessDefinition = deployedProcessDefinitions.values()
                                .stream().findFirst();
                        if (optionalDeployedProcessDefinition.isEmpty()) {
                            throw new AtmLayerException("Empty Process Definitions from deploy payload", Response.Status.INTERNAL_SERVER_ERROR, DEPLOY_ERROR);
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
                            throw new AtmLayerException("Empty Decision Definitions from deploy payload", Response.Status.INTERNAL_SERVER_ERROR, DEPLOY_ERROR);
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
                            return Uni.createFrom().failure(new AtmLayerException("Failed to save Workflow Resource in Object Store. Workflow Resource creation aborted", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
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
                        throw new AtmLayerException("A Workflow Resource with the same definitionKey already exists", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS);
                    }
                    return saveAndUpload(workflowResource, file, filename)
                            .onItem().transformToUni(workflow -> this.findById(workflow.getWorkflowResourceId())
                                    .onItem().transformToUni(optionalWorkflow -> {
                                        if (optionalWorkflow.isEmpty()) {
                                            return Uni.createFrom().failure(new AtmLayerException("Sync problem on Workflow Resource creation", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
                                        }
                                        return Uni.createFrom().item(optionalWorkflow.get());
                                    }));
                }));
    }

    @WithTransaction
    @Override
    public Uni<Boolean> delete(UUID uuid) {
        log.info("Deleting Workflow Resource with id {}", uuid.toString());
        return this.findById(uuid)
                .onItem()
                .transformToUni(Unchecked.function(x -> {
                    if (x.isEmpty()) {
                        throw new AtmLayerException(String.format("Workflow Resource with id %s does not exists", uuid), Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    if (!StatusEnum.isEditable(x.get().getStatus())) {
                        throw new AtmLayerException(String.format("Workflow Resource with id %s is in status %s and cannot be " +
                                "deleted. Only Workflow Resource files in status %s can be deleted", uuid, x.get().getStatus(), StatusEnum.getUpdatableAndDeletableStatuses()), Response.Status.BAD_REQUEST, AppErrorCodeEnum.WORKFLOW_RESOURCE_CANNOT_BE_DELETED_FOR_STATUS);
                    }
                    return Uni.createFrom().item(x.get());
                })).onItem().transformToUni(y -> this.workflowResourceRepository.deleteById(uuid));
    }

    @Override
    public Uni<List<WorkflowResource>> getAll() {
        return this.workflowResourceRepository.findAll().list();
    }

    @Override
    @WithSession
    public Uni<List<WorkflowResource>> getAllFiltred(String deployedFileName, StatusEnum status, int pageIndex, int pageSize){
        Map<String, Object> filters = new HashMap<>();
        filters.put("deployedFileName", deployedFileName);
        if(status!=null) filters.put("status", status);
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
                        throw new AtmLayerException("Workflow Resource already present", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS);
                    }
                    workflowFound.setSha256(shaUpdateFile);
                    if (!workflowFound.getDefinitionKey().equals(definitionKey)) {
                        throw new AtmLayerException(String.format("The definition key in your Workflow Resource: %s does not match the Workflow Resource you are trying to update", definitionKey), Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_CANNOT_BE_UPDATED);
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
                        throw new AtmLayerException("The referenced workflow resource does not exist", Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    WorkflowResource workflowResourceToRollBack = workflow.get();
                    if (workflowResourceToRollBack.getStatus().getValue().equals(StatusEnum.DEPLOYED.getValue())) {
                        throw new AtmLayerException("Cannot rollback: the referenced resource is the latest version deployed", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_CANNOT_BE_ROLLED_BACK);
                    }
                    UUID deploymentId = workflowResourceToRollBack.getDeploymentId();
                    if (deploymentId == null) {
                        throw new AtmLayerException("CamundaDefinitionId of the referenced resource is null: cannot rollback", Response.Status.NOT_FOUND, WORKFLOW_RESOURCE_NOT_DEPLOYED_CANNOT_ROLLBACK);
                    }
                    return processClient.getDeployedResource(deploymentId.toString())
                            .onFailure()
                            .recoverWithUni(exception ->
                                    Uni.createFrom().failure(new AtmLayerException("Error retrieving workflow resource from Process", Response.Status.INTERNAL_SERVER_ERROR, DEPLOYED_FILE_WAS_NOT_RETRIEVED)))
                            .onItem()
                            .transformToUni(Unchecked.function(file -> update(id, file, true)));
                }));
    }
}
