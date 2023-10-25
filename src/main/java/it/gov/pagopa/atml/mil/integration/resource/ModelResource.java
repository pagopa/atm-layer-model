package it.gov.pagopa.atml.mil.integration.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atml.mil.integration.entity.BpmnVersion;
import it.gov.pagopa.atml.mil.integration.model.AssociationMetadata;
import it.gov.pagopa.atml.mil.integration.dto.BpmnCreationDto;
import it.gov.pagopa.atml.mil.integration.dto.BpmnAssociationDto;
import it.gov.pagopa.atml.mil.integration.service.impl.ModelServiceImpl;
import it.gov.pagopa.atml.mil.integration.utils.BpmnDtoConverter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
@Path("/model")
@Tag(name = "Model", description = "Model operations")
public class ModelResource {

    private final Logger logger = LoggerFactory.getLogger(InfoResource.class);

    @Inject
    ModelServiceImpl modelService;



    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getEncodedFile(@QueryParam("string") String s) throws IOException {
        String xml = modelService.decodeBase64(s);
        logger.info("String file: "+xml);
        return "String file: "+xml;
    }

    @POST
    @Path("/association")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<AssociationMetadata> associateBPMN(@RequestBody @Valid BpmnAssociationDto modelEntity) throws NoSuchAlgorithmException, IOException {
        //modelService.calculateSha256(modelEntity.getFile()); calcolo sha256
        return Uni.createFrom().item(modelEntity.getAssociationMetadata());
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnVersion> createBPMN(@RequestBody @Valid BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        return Uni.createFrom().item(BpmnDtoConverter.converter(bpmnCreationDto));
    }
}
