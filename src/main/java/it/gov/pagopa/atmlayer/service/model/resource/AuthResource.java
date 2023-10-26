package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.model.mil.AuthPayload;
import it.gov.pagopa.atmlayer.service.model.model.mil.MILAccessToken;
import it.gov.pagopa.atmlayer.service.model.service.impl.MILAuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class that expose the API to retrieve info about the service
 */
@Path("/auth")
@Tag(name = "Auth", description = "Auth operations")
public class AuthResource {

    private final Logger logger = LoggerFactory.getLogger(AuthResource.class);

    @Inject
    MILAuthService milAuthService;


    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<MILAccessToken> getToken(AuthPayload authPayload) {

        return this.milAuthService.getToken(authPayload, "6762543c-2660-4622-b4d4-8b2bc596df29", "06789", "64874412");
    }
}
