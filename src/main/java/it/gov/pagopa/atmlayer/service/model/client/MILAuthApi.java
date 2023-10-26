package it.gov.pagopa.atmlayer.service.model.client;

import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.model.mil.AuthPayload;
import it.gov.pagopa.atmlayer.service.model.model.mil.MILAccessToken;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/mil-auth")
@RegisterRestClient(configKey = "mil-auth-api")
@ClientHeaderParam(name = "RequestId", value = "{requestId}")
@ClientHeaderParam(name = "AcquirerId", value = "{acquirerId}")
@ClientHeaderParam(name = "Channel", value = "${common.header.channel}")
@ClientHeaderParam(name = "TerminalId", value = "{terminalId}")
public interface MILAuthApi {

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Uni<MILAccessToken> getToken(AuthPayload form, @NotBody String requestId, @NotBody String acquirerId, @NotBody String terminalId);
}
