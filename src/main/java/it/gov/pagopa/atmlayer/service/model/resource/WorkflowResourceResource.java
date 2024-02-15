package it.gov.pagopa.atmlayer.service.model.resource;

import io.opentelemetry.api.trace.Tracer;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.dto.FileS3Dto;
import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceFileMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.WorkflowResourceMapper;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.WORKFLOW_FILE_DOES_NOT_EXIST;

@ApplicationScoped
@Path("/workflow-resource")
@Tag(name = "Workflow Resource", description = "Workflow Resource Operations")
@Slf4j
public class WorkflowResourceResource {
    @Inject
    WorkflowResourceService workflowResourceService;
    @Inject
    WorkflowResourceMapper workflowResourceMapper;
    @Inject
    ResourceFileMapper resourceFileMapper;
    @Inject
    Tracer tracer;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<WorkflowResourceDTO>> getAll() {
        return this.workflowResourceService.getAll()
                .onItem()
                .transform(Unchecked.function(list -> {
                    if (list.isEmpty()) {
                        log.info("No Workflow Resource files saved in database");
                    }
                    return workflowResourceMapper.toDTOList(list);
                }));
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<PageInfo<WorkflowResourceFrontEndDTO>> getAllFiltered(@QueryParam("pageIndex") @DefaultValue("0")
                                                                     @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "0")) Integer page,
                                                                     @QueryParam("pageSize") @DefaultValue("10")
                                                                     @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "1")) Integer size,
                                                                     @QueryParam("status")
                                                                     @Schema(implementation = String.class, type = SchemaType.STRING, enumeration = {"CREATED", "WAITING_DEPLOY", "UPDATED_BUT_NOT_DEPLOYED", "DEPLOYED", "DEPLOY_ERROR"}) StatusEnum status,
                                                                     @QueryParam("workflowResourceId") UUID workflowResourceId,
                                                                     @QueryParam("deployedFileName") String deployedFileName,
                                                                     @QueryParam("definitionKey") String definitionKey,
                                                                     @QueryParam("resourceType") DeployableResourceType resourceType,
                                                                     @QueryParam("sha256") String sha256,
                                                                     @QueryParam("definitionVersionCamunda") String definitionVersionCamunda,
                                                                     @QueryParam("camundaDefinitionId") String camundaDefinitionId,
                                                                     @QueryParam("description") String description,
                                                                     @QueryParam("resource") String resource,
                                                                     @QueryParam("deploymentId") UUID deploymentId,
                                                                     @QueryParam("fileName") String fileName) {
        return this.workflowResourceService.getAllFiltered(page, size, status, workflowResourceId, deployedFileName, definitionKey, resourceType, sha256, definitionVersionCamunda, camundaDefinitionId, description, resource, deploymentId, fileName)
                .onItem()
                .transform(Unchecked.function(pagedList -> {
                    if (pagedList.getResults().isEmpty()) {
                        log.info("No Workflow Resource file meets the applied filters");
                    }
                    return workflowResourceMapper.toFrontEndDTOListPaged(pagedList);
                }));
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<WorkflowResourceDTO> getById(@PathParam("uuid") UUID id) {
        return this.workflowResourceService.findById(id)
                .onItem()
                .transform(Unchecked.function(x -> {
                    if (x.isEmpty()) {
                        throw new AtmLayerException(Response.Status.NOT_FOUND, WORKFLOW_FILE_DOES_NOT_EXIST);
                    }
                    return workflowResourceMapper.toDTO(x.get());
                }));
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    public Uni<WorkflowResourceDTO> create(@RequestBody(required = true) @Valid WorkflowResourceCreationDto workflowResourceCreationDto) throws NoSuchAlgorithmException, IOException {
        WorkflowResource workflowResource = workflowResourceMapper.toEntityCreation(workflowResourceCreationDto);
        return this.workflowResourceService.createWorkflowResource(workflowResource, workflowResourceCreationDto.getFile(), workflowResourceCreationDto.getFilename())
                .onItem().transformToUni(bpmn -> Uni.createFrom().item(this.workflowResourceMapper.toDTO(bpmn)));
    }

    @POST
    @Path("/deploy/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<WorkflowResourceDTO> deploy(@PathParam("uuid") UUID uuid) {
        return this.workflowResourceService.findById(uuid)
                .onItem()
                .transformToUni(resource -> this.workflowResourceService.deploy(uuid, resource))
                .onItem().transformToUni(resourceDeployed -> Uni.createFrom().item(this.workflowResourceMapper.toDTO(resourceDeployed)));
    }

    @POST
    @Path("/disable/{uuid}")
    public Uni<Void> disable(@PathParam("uuid") UUID uuid) {
        return this.workflowResourceService.disable(uuid);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uuid}")
    public Uni<Void> delete(@PathParam("uuid") UUID uuid) {
        return this.workflowResourceService.delete(uuid)
                .onItem().ignore().andSwitchTo(Uni.createFrom().voidItem());
    }

    @PUT
    @Path("/update/{uuid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<WorkflowResourceDTO> update(@RequestBody(required = true) @FormParam("file") @NotNull(message = "input file is required") File file,
                                           @PathParam("uuid") UUID uuid) throws NoSuchAlgorithmException, IOException {
        return workflowResourceService.update(uuid, file, false)
                .onItem()
                .transformToUni(updatedWorkflowResource -> Uni.createFrom().item(workflowResourceMapper.toDTO(updatedWorkflowResource)));
    }

    @PUT
    @Path("/rollback/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<WorkflowResourceDTO> rollback(@PathParam("uuid") UUID uuid) {
        return workflowResourceService.rollback(uuid)
                .onItem()
                .transformToUni(rolledBackWorkflowResource -> Uni.createFrom().item(workflowResourceMapper.toDTO(rolledBackWorkflowResource)));
    }

    @GET
    @Path("/download/{uuid}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Multi<Buffer> download(@PathParam("uuid") UUID uuid) {
        return this.workflowResourceService.download(uuid);
    }

    @GET
    @Path("/downloadFrontEnd/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<FileS3Dto> downloadFrontEnd(@PathParam("uuid") UUID uuid) {
        return this.workflowResourceService.downloadForFrontEnd(uuid)
                .onItem().transform(FileS3Dto::new);
    }

}
