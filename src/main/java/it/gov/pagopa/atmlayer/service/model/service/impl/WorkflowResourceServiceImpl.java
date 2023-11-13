package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedProcessInfoDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.DEPLOY_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_CANNOT_BE_UPDATED;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_RESOURCE_FILE_WITH_SAME_CONTENT_ALREADY_EXIST;
import static it.gov.pagopa.atmlayer.service.model.utils.EnumConverter.convertEnum;
import static it.gov.pagopa.atmlayer.service.model.utils.FileUtils.extractIdValue;

@ApplicationScoped
@Slf4j
public class WorkflowResourceServiceImpl implements WorkflowResourceService {

    @Inject
    WorkflowResourceRepository workflowResourceRepository;

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
                                        "One or some of the referenced Workflow Resource files do not exists: %s", uuid);
                                throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                                        WORKFLOW_FILE_DOES_NOT_EXIST);
                            }
                            WorkflowResource workflowResource = optionalWorkflowResource.get();
                            return workflowResource.getStatus().equals(StatusEnum.CREATED) || workflowResource.getStatus()
                                    .equals(StatusEnum.DEPLOY_ERROR);
                        })
                );
    }

    @WithTransaction
    public Uni<WorkflowResource> setWorkflowResourceVersionStatus(UUID uuid, StatusEnum status) {
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
    public Uni<WorkflowResource> deploy(Optional<WorkflowResource> optionalWorkflowResource) {
        if (optionalWorkflowResource.isEmpty()) {
            String errorMessage = "The referenced Workflow Resource file can not be deployed";
            throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST,
                    AppErrorCodeEnum.WORKFLOW_RESOURCE_FILE_CANNOT_BE_DEPLOYED);
        }
        UUID uuid = optionalWorkflowResource.get().getWorkflowResourceId();
        DeployableResourceType resourceType = optionalWorkflowResource.get().getResourceType();
        return this.setWorkflowResourceVersionStatus(uuid, StatusEnum.WAITING_DEPLOY)
                .onItem()
                .transformToUni(workflowWaiting -> {
                    ResourceFile resourceFile = workflowWaiting.getResourceFile();
                    if (Objects.isNull(resourceFile) || StringUtils.isBlank(resourceFile.getStorageKey())) {
                        String errorMessage = String.format("No file associated to Workflow Resource or no storage key found: %s", uuid);
                        log.error(errorMessage);
                        return Uni.createFrom().failure
                                (new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.WORKFLOW_RESOURCE_CANNOT_BE_DELETED_FOR_STATUS));
                    }
                    return this.workflowResourceStorageService.generatePresignedUrl(resourceFile.getStorageKey())
                            .onFailure().recoverWithUni(failure -> {
                                log.error(failure.getMessage());
                                return this.setWorkflowResourceVersionStatus(uuid, StatusEnum.DEPLOY_ERROR)
                                        .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Error in Workflow Resource deploy. Fail to generate presigned URL", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                            });
                })
                .onItem().transformToUni(presignedUrl -> processClient.deploy(presignedUrl.toString(), resourceType.name())
                        .onFailure().recoverWithUni(failure -> {
                            log.error(failure.getMessage());
                            return this.setWorkflowResourceVersionStatus(uuid, StatusEnum.DEPLOY_ERROR)
                                    .onItem().transformToUni(x -> Uni.createFrom().failure(new AtmLayerException("Error in Workflow Resource deploy. Communication with Process Service failed", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500)));
                        })
                        .onItem()
                        .transformToUni(response -> this.setDeployInfo(uuid, response)));
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
                    Map<String, DeployedProcessInfoDto> deployedProcessDefinitions = response.getDeployedProcessDefinitions();
                    Optional<DeployedProcessInfoDto> optionalDeployedProcessInfo = deployedProcessDefinitions.values()
                            .stream().findFirst();
                    if (optionalDeployedProcessInfo.isEmpty()) {
                        throw new AtmLayerException("Empty process infos from deploy payload", Response.Status.INTERNAL_SERVER_ERROR, DEPLOY_ERROR);
                    }
                    DeployedProcessInfoDto deployedProcessInfo = optionalDeployedProcessInfo.get();
                    workflowResource.setDefinitionVersionCamunda(deployedProcessInfo.getVersion());
                    workflowResource.setDeploymentId(deployedProcessInfo.getDeploymentId());
                    workflowResource.setCamundaDefinitionId(deployedProcessInfo.getId());
                    workflowResource.setDeployedFileName(deployedProcessInfo.getName());
                    workflowResource.setDescription(deployedProcessInfo.getDescription());
                    workflowResource.setResource(deployedProcessInfo.getResource());
                    workflowResource.setStatus(StatusEnum.DEPLOYED);
                    return this.workflowResourceRepository.persist(workflowResource);
                }));
    }

    @Override
    @WithTransaction
    public Uni<WorkflowResource> saveAndUpload(WorkflowResource workflowResource, File file, String filename) {
        return this.save(workflowResource)
                .onItem().transformToUni(record -> this.workflowResourceStorageService.uploadFile(workflowResource, file, filename)
                        .onFailure().recoverWithUni(failure -> {
                            log.error(failure.getMessage());
                            return Uni.createFrom().failure(new AtmLayerException("Failed to save Workflow Resource in Object Store. Workflow Resource creation aborted", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                        })
                        .onItem().transformToUni(putObjectResponse -> {
                            log.info("Completed Workflow Resource Creation");
                            return Uni.createFrom().item(record);
                        }));
    }

    @Override
    public Uni<WorkflowResource> createWorkflowResource(WorkflowResource workflowResource, File file, String filename) {
        String definitionKey = extractIdValue(file, workflowResource.getResourceType());
        workflowResource.setDefinitionKey(definitionKey);
        return findByDefinitionKey(definitionKey)
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException("A Workflow Resource with the same definitionKey already exists", Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS);
                    }
                    return saveAndUpload(workflowResource, file, filename)
                            .onItem().transformToUni(workflow -> {
                                return this.findById(workflow.getWorkflowResourceId())
                                        .onItem().transformToUni(optionalWorkflow -> {
                                            if (optionalWorkflow.isEmpty()) {
                                                return Uni.createFrom().failure(new AtmLayerException("Sync problem on Workflow Resource creation", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500));
                                            }
                                            return Uni.createFrom().item(optionalWorkflow.get());
                                        });
                            });
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
    public Uni<WorkflowResource> update(UUID id, File file, WorkflowResource workflowResource) {
        log.info("Updating Workflow Resource with id {}", id.toString());
        DeployableResourceType deployableResourceType = workflowResource.getResourceType();
        String definitionKey = extractIdValue(file, deployableResourceType);
        String storageKey = workflowResource.getResourceFile().getStorageKey();
        log.info("storage key {}", storageKey);
        if (!workflowResource.getDefinitionKey().equals(definitionKey)) {
            throw new AtmLayerException(String.format("Workflow Resource with type %s does not match the Workflow Resource you are trying to update", definitionKey), Response.Status.BAD_REQUEST, WORKFLOW_RESOURCE_CANNOT_BE_UPDATED);
        }
        return Uni.createFrom().item(workflowResourceStorageService.updateFile(workflowResource, file))
                .onFailure().recoverWithUni(failure -> {
                    log.error(failure.getMessage());
                    return Uni.createFrom().failure(new AtmLayerException("Failed to save Workflow Resource in Object Store. Workflow Resource update aborted", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                })
                .onItem().transformToUni(putObjectResponse -> {
                    log.info("Completed Workflow Resource Update");
                    return Uni.createFrom().item(workflowResource);
                });
    }
}
