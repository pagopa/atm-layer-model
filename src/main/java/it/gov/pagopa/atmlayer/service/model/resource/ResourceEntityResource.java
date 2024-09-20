package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceMultipleCreationDtoJSON;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceEntityMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceFileMapper;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.RESOURCE_FILE_DOES_NOT_EXIST;

@ApplicationScoped
@Path("/resources")
@Tag(name = "RESOURCES", description = "RESOURCES operations")
@Slf4j
public class ResourceEntityResource {

    private final ResourceEntityMapper resourceEntityMapper;
    private final ResourceEntityService resourceEntityService;

    @Inject
    public ResourceEntityResource(ResourceEntityMapper resourceEntityMapper, ResourceFileMapper resourceFileMapper,
                                  ResourceEntityService resourceEntityService) {
        this.resourceEntityMapper = resourceEntityMapper;
        this.resourceEntityService = resourceEntityService;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    @Operation(
            operationId = "createResource",
            description = "creazione file"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = ResourceDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<ResourceDTO> createResource(
            @RequestBody(required = true) @Valid ResourceCreationDto resourceCreationDto)
            throws NoSuchAlgorithmException, IOException {
        ResourceEntity resourceEntity = resourceEntityMapper.toEntityCreation(resourceCreationDto);
        return resourceEntityService.createResource(resourceEntity, resourceCreationDto.getFile(),
                        resourceCreationDto.getFilename(), resourceCreationDto.getPath(), resourceCreationDto.getDescription())
                .onItem()
                .transformToUni(resource -> Uni.createFrom().item(resourceEntityMapper.toDTO(resource)));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/multiple")
    public Uni<List<String>> createResourceMultiple(@RequestBody(required = true) ResourceMultipleCreationDtoJSON resourceMultipleCreationDto) {
        log.info("start multiple with form data = {}", resourceMultipleCreationDto);

        List<ResourceCreationDto> resourceCreationDtoList = resourceEntityMapper.convertToResourceCreationDtoList(resourceMultipleCreationDto);
        List<ResourceEntity> resourceEntityList = resourceEntityMapper.toEntityCreationList(resourceCreationDtoList);
        return resourceEntityService.createResourceMultiple(resourceEntityList, resourceCreationDtoList);

    }

    @PUT
    @Path("/{uuid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "updateResource",
            description = "aggiorna file"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = ResourceDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<ResourceDTO> updateResource(@RequestBody(required = true) @FormParam("file") File file,
                                           @PathParam("uuid") UUID uuid) {
        return resourceEntityService.updateResource(uuid, file)
                .onItem()
                .transformToUni(updatedResource -> Uni.createFrom().item(resourceEntityMapper.toDTO(updatedResource)));
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "getResourceById",
            description = "cerca per Id"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = ResourceDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<ResourceDTO> getById(@PathParam("uuid") UUID uuid) {
        return this.resourceEntityService.findByUUID(uuid)
                .onItem()
                .transform(Unchecked.function(x -> {
                    if (x.isEmpty()) {
                        throw new AtmLayerException(Response.Status.NOT_FOUND, RESOURCE_FILE_DOES_NOT_EXIST);
                    }
                    return resourceEntityMapper.toDTO(x.get());
                }));
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "getResourceFiltered",
            description = "filtra tra tutti i Resource file"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = PageInfo.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<PageInfo<ResourceFrontEndDTO>> getResourceFiltered(@QueryParam("pageIndex") @DefaultValue("0")
                                                                  @Parameter(required = true, schema = @Schema(minimum = "1", maximum = "10000")) int pageIndex,
                                                                  @QueryParam("pageSize") @DefaultValue("10")
                                                                  @Parameter(required = true, schema = @Schema(minimum = "1", maximum = "100")) int pageSize,
                                                                  @QueryParam("resourceId") UUID resourceId,
                                                                  @QueryParam("sha256") @Schema(format = "byte", maxLength = 255) String sha256,
                                                                  @QueryParam("noDeployableResourceType") NoDeployableResourceType noDeployableResourceType,
                                                                  @QueryParam("fileName") @Schema(format = "byte", maxLength = 255) String fileName,
                                                                  @QueryParam("storageKey") @Schema(format = "byte", maxLength = 255) String storageKey,
                                                                  @QueryParam("extension") @Schema(format = "byte", maxLength = 255) String extension) {
        return resourceEntityService.findResourceFiltered(pageIndex, pageSize, resourceId, sha256, noDeployableResourceType, fileName, storageKey, extension)
                .onItem()
                .transform(Unchecked.function(pagedList -> {
                    if (pagedList.getResults().isEmpty()) {
                        log.info("No Resource entity meets the applied filters");
                    }
                    return resourceEntityMapper.toFrontEndDTOPaged(pagedList);

                }));
    }

    @POST
    @Path("/disable/{uuid}")
    @Operation(
            operationId = "disableResource",
            description = "disabilita file"
    )
    @APIResponse(responseCode = "204", description = "Ok")
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<Void> disable(@PathParam("uuid") UUID uuid) {
        return this.resourceEntityService.disable(uuid);
    }

    @DELETE
    @Path("/{uuid}")
    @Operation(
            operationId = "deleteResource",
            description = "elimina file"
    )
    @APIResponse(responseCode = "204", description = "Ok")
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<Void> deleteResource(@PathParam("uuid") UUID uuid) {
        return resourceEntityService.deleteResource(uuid);
    }

}

