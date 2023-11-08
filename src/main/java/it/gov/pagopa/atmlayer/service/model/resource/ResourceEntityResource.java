package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceEntityMapper;
import it.gov.pagopa.atmlayer.service.model.model.ResourceEntityDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import it.gov.pagopa.atmlayer.service.model.service.impl.ResourceEntityServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
@Path("/resources")
@Tag(name = "RESOURCES", description = "RESOURCES operations")
@Slf4j
public class ResourceEntityResource {
    @Inject
    ResourceEntityMapper resourceEntityMapper;
    @Inject
    ResourceEntityService resourceEntityService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    public Uni<ResourceEntityDTO> createResource(@RequestBody(required = true) @Valid ResourceCreationDto resourceCreationDto) throws NoSuchAlgorithmException, IOException {
        ResourceEntity resourceEntity = resourceEntityMapper.toEntityCreation(resourceCreationDto);
        return resourceEntityService.createResource(resourceEntity, resourceCreationDto.getFile(), resourceCreationDto.getFilename())
                //.onItem().transformToUni(resource -> Uni.createFrom().item(resource));
                //quando il metodo toDto è implementato nel mapper, sostituire con:
                .onItem().transformToUni(resource -> Uni.createFrom().item(resourceEntityMapper.toDTO(resource)));
    }
}
