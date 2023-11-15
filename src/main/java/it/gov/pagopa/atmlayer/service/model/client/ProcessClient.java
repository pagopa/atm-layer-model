package it.gov.pagopa.atmlayer.service.model.client;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "process-deploy")
public interface ProcessClient {
    @POST
    @Path("/api/v1/processes/deploy/{type}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<DeployResponseDto> deploy(@FormParam("url") String url, @PathParam("type") String type);
}
