package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.dto.FileS3Dto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowResourceService {

    Uni<WorkflowResource> save(WorkflowResource workflowResource);

    Uni<Optional<WorkflowResource>> findBySHA256(String sha256);

    Uni<Optional<WorkflowResource>> findById(UUID id);

    Uni<Optional<WorkflowResource>> findByDefinitionKey(String definitionKey);

    Uni<WorkflowResource> deploy(UUID id, Optional<WorkflowResource> workflowResource);

    Uni<WorkflowResource> saveAndUpload(WorkflowResource workflowResource, File file, String filename);

    Uni<WorkflowResource> createWorkflowResource(WorkflowResource workflowResource, File file, String filename);

    Uni<Void> disable(UUID uuid);

    Uni<Boolean> delete(UUID uuid);

    Uni<List<WorkflowResource>> getAll();

    Uni<PageInfo<WorkflowResource>> getAllFiltered(int page, int size, StatusEnum status, UUID workflowResourceId, String deployedFileName, String definitionKey, DeployableResourceType resourceType, String sha256, String definitionVersionCamunda, String camundaDefinitionId, String description, String resource, UUID deploymentId, String fileName);

    Uni<WorkflowResource> update(UUID id, File file,boolean isRollback) throws NoSuchAlgorithmException, IOException;

    Uni<WorkflowResource> rollback(UUID id);

    Multi<Buffer> download(UUID id);

    Uni<String> downloadForFrontEnd(UUID id);
}
