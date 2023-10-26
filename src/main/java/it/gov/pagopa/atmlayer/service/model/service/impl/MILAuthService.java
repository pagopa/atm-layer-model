package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.rest.client.reactive.NotBody;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.client.MILAuthApi;
import it.gov.pagopa.atmlayer.service.model.model.mil.AuthPayload;
import it.gov.pagopa.atmlayer.service.model.model.mil.MILAccessToken;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Slf4j
public class MILAuthService {

    @RestClient
    MILAuthApi milAuthApi;

    public Uni<MILAccessToken> getToken(AuthPayload payload, String requestId, @NotBody String acquirerId, @NotBody String terminalId) {
        return milAuthApi.getToken(payload, requestId, acquirerId, terminalId);
    }

}
