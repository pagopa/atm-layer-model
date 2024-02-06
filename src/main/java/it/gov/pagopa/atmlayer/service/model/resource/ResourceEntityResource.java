package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
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
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
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
    @Inject
    ResourceEntityMapper resourceEntityMapper;
    @Inject
    ResourceFileMapper resourceFileMapper;
    @Inject
    ResourceEntityService resourceEntityService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    public Uni<ResourceDTO> createResource(
            @RequestBody(required = true) @Valid ResourceCreationDto resourceCreationDto)
            throws NoSuchAlgorithmException, IOException {
        ResourceEntity resourceEntity = resourceEntityMapper.toEntityCreation(resourceCreationDto);
        return resourceEntityService.createResource(resourceEntity, resourceCreationDto.getFile(),
                        resourceCreationDto.getFilename(), resourceCreationDto.getPath())
                .onItem()
                .transformToUni(resource -> Uni.createFrom().item(resourceEntityMapper.toDTO(resource)));
    }

    @PUT
    @Path("/{uuid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ResourceDTO> updateResource(@RequestBody(required = true) @FormParam("file") File file,
                                           @PathParam("uuid") UUID uuid) {
        return resourceEntityService.updateResource(uuid, file)
                .onItem()
                .transformToUni(updatedResource -> Uni.createFrom().item(resourceEntityMapper.toDTO(updatedResource)));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<ResourceDTO>> getAll() {
        return this.resourceEntityService.getAll()
                .onItem()
                .transform(Unchecked.function(list -> {
                    if (list.isEmpty()) {
                        log.info("No Resource files saved in database");
                    }
                    return resourceEntityMapper.toDTOList(list);
                }));
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
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
    public Uni<PageInfo<ResourceFrontEndDTO>> getResourceFiltered(@QueryParam("pageIndex") @DefaultValue("0")
                                                                      @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "0")) int pageIndex,
                                                                  @QueryParam("pageSize") @DefaultValue("10")
                                                                      @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "1")) int pageSize,
                                                                  @QueryParam("resourceId") UUID resourceId,
                                                                  @QueryParam("sha256") String sha256,
                                                                  @QueryParam("noDeployableResourceType") NoDeployableResourceType noDeployableResourceType,
                                                                  @QueryParam("fileName") String fileName,
                                                                  @QueryParam("storageKey") String storageKey,
                                                                  @QueryParam("extension") String extension){
        return resourceEntityService.findResourceFiltered(pageIndex, pageSize, resourceId, sha256, noDeployableResourceType, fileName, storageKey, extension)
                .onItem()
                .transform(Unchecked.function(pagedList -> {
                    if (pagedList.getResults().isEmpty()) {
                        log.info("No Resource entity meets the applied filters");
                    }
                    return resourceEntityMapper.toFrontEndDTOPaged(pagedList);

                }));
    }
}
