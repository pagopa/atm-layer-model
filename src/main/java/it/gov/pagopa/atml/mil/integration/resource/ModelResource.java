package it.gov.pagopa.atml.mil.integration.resource;

import it.gov.pagopa.atml.mil.integration.service.impl.ModelService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ApplicationScoped
@Path("/model")
@Tag(name = "Model", description = "Model operations")
public class ModelResource {

    private final Logger logger = LoggerFactory.getLogger(InfoResource.class);

    @Inject
    ModelService modelService;

    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String info(@BeanParam String s) throws IOException {
        String xml = modelService.decodeBase64(s);
        logger.info("String file: "+xml);
        return "your file has the following hashcode: "+xml;
    }
}
