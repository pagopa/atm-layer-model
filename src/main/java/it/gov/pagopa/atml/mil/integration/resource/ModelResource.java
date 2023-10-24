package it.gov.pagopa.atml.mil.integration.resource;

import it.gov.pagopa.atml.mil.integration.model.dto.ModelDto;
import it.gov.pagopa.atml.mil.integration.service.impl.ModelService;
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
    ModelService modelService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getEncodedFile(@QueryParam("string") String s) throws IOException {
        String xml = modelService.decodeBase64(s);
        logger.info("String file: "+xml);
        return "String file: "+xml;
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public String saveBPMN(@RequestBody ModelDto modelEntity) throws NoSuchAlgorithmException, IOException {
        return modelService.calculateSha256(modelEntity.getFile())+"\n"+"these are your metadata: "+modelEntity.getMetadata().toString();
    }
}
