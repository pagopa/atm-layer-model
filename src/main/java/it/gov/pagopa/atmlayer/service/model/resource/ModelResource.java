package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.service.BpmnModelService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnDtoMapper;
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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@ApplicationScoped
@Path("/bpmn")
@Tag(name = "BPMN", description = "BPMN operations")
@Slf4j
public class ModelResource {


    @Inject
    BpmnModelService bpmnModelService;


//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getEncodedFile(@QueryParam("string") String s) throws IOException {
//        String xml = modelService.decodeBase64(s);
//        logger.info("String file: " + xml);
//        return "String file: " + xml;
//    }

    @PUT
    @Path("/associations/{UUID}/version/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnVersion> associateBPMN(@PathParam("UUID") String uuid, @PathParam("version") String version, @RequestBody @Valid BpmnAssociationDto bpmnAssociationDto) throws NoSuchAlgorithmException, IOException {
        UUID uuidValue = UUID.fromString(uuid);
        int versionValue = Integer.parseInt(version);
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(uuidValue, versionValue);
        BpmnVersion bpmnVersion = new BpmnVersion();
        return bpmnModelService.findByPk(bpmnVersionPK);
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnVersion> createBPMN(@RequestBody @Valid BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = BpmnDtoMapper.toBpmnVersion(bpmnCreationDto);
        return bpmnModelService.save(bpmnVersion);
    }
}
