package it.gov.pagopa.atmlayer.service.model.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "engine-test", baseUri = "http://localhost:8080/engine-rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ClientHeaderParam(name = "Authorization", value = "Basic YWRtaW46QXVyaWdhTnR0LjIwMjM=")
public interface EngineClientTest {
    @POST
    @Path("/process-definition/{definitionId}/start")
    Response startProcessWithPayload(@PathParam("definitionId") String definitionId, String payload);
}
