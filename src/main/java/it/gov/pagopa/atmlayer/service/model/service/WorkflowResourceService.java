package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;

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

    Uni<WorkflowResource> deploy(Optional<WorkflowResource> optionalWorkflowResource);

    Uni<WorkflowResource> saveAndUpload(WorkflowResource workflowResource, File file, String filename);

    Uni<WorkflowResource> createWorkflowResource(WorkflowResource workflowResource, File file, String filename);

    Uni<Boolean> delete(UUID uuid);

    Uni<List<WorkflowResource>> getAll();

    Uni<ResourceFile> update(UUID id, File file) throws NoSuchAlgorithmException, IOException;
}
