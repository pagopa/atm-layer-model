package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.mapper.WorkflowResourceMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@ApplicationScoped
@Path("/workflow-resource")
@Tag(name = "Workflow Resource", description = "Workflow Resource Operations")
@Slf4j
public class WorkflowResourceResource {

    @Inject
    WorkflowResourceService workflowResourceService;

    @Inject
    WorkflowResourceMapper workflowResourceMapper;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    public Uni<WorkflowResourceDTO> createWorkflowResource(@RequestBody(required = true) @Valid WorkflowResourceCreationDto workflowResourceCreationDto) throws NoSuchAlgorithmException, IOException {
        WorkflowResource workflowResource = workflowResourceMapper.toEntityCreation(workflowResourceCreationDto);
        return this.workflowResourceService.createWorkflowResource(workflowResource, workflowResourceCreationDto.getFile(), workflowResourceCreationDto.getFilename())
                .onItem().transformToUni(bpmn -> Uni.createFrom().item(this.workflowResourceMapper.toDTO(bpmn)));
    }

    @POST
    @Path("/deploy/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<WorkflowResourceDTO> deployBPMN(@PathParam("uuid") UUID uuid) {

        return this.workflowResourceService.deploy(uuid)
                .onItem().transformToUni(workflow -> Uni.createFrom().item(this.workflowResourceMapper.toDTO(workflow)));
    }
}
