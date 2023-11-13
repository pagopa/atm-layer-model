package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceEntityMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceFileMapper;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFileDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

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
            resourceCreationDto.getFilename(),resourceCreationDto.getPath())
        .onItem()
        .transformToUni(resource -> Uni.createFrom().item(resourceEntityMapper.toDTO(resource)));
  }

//  @PUT
//  @Path("/{uuid}")
//  @Consumes(MediaType.MULTIPART_FORM_DATA)
//  @Produces(MediaType.APPLICATION_JSON)
//  @NonBlocking
//  public Uni<ResourceFileDTO> updateResource(@RequestBody(required = true) @Valid ResourceCreationDto resourceCreationDto,
//                                             @PathParam("uuid")UUID uuid) throws NoSuchAlgorithmException, IOException {
//    ResourceEntity resourceEntity = resourceEntityMapper.toEntityCreation(resourceCreationDto);
//    return resourceEntityService.updateResource(uuid,resourceEntity,resourceCreationDto.getFile(),
//                    resourceCreationDto.getFilename(),resourceCreationDto.getPath())
//            .onItem()
//            .transformToUni(updateResourceFile -> Uni.createFrom().item(resourceFileMapper.toDTO(updateResourceFile)));
//  }

  @PUT
  @Path("/{uuid}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @NonBlocking
  public Uni<ResourceFileDTO> updateResource(@RequestBody(required = true) File file,
                                             @PathParam("uuid")UUID uuid) {
    return resourceEntityService.updateResource(uuid,file)
            .onItem()
            .transformToUni(updateResourceFile -> Uni.createFrom().item(resourceFileMapper.toDTO(updateResourceFile)));
  }


}
