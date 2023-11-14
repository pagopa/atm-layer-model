package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceEntityMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceFileMapper;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFileDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
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
    public Uni<ResourceFileDTO> updateResource(@RequestBody(required = true) @FormParam("file") File file,
                                               @PathParam("uuid") UUID uuid) {
        return resourceEntityService.updateResource(uuid, file)
                .onItem()
                .transformToUni(updateResourceFile -> Uni.createFrom().item(resourceFileMapper.toDTO(updateResourceFile)));
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
  public Uni<ResourceDTO> getById(@PathParam("uuid") UUID uuid){
    return this.resourceEntityService.findByUUID(uuid)
        .onItem()
        .transform(Unchecked.function(x -> {
          if (x.isEmpty()) {
            throw new AtmLayerException(Response.Status.NOT_FOUND, RESOURCE_FILE_DOES_NOT_EXIST);
          }
          return resourceEntityMapper.toDTO(x.get());
        }));
  }
}
