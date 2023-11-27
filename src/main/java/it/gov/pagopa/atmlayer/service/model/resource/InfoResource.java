package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import it.gov.pagopa.atmlayer.service.model.model.InfoResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class that expose the API to retrieve info about the service
 */
@Path("/info")
@Tag(name = "Info", description = "Application info")
public class InfoResource {

    private final Logger logger = LoggerFactory.getLogger(InfoResource.class);

    @ConfigProperty(name = "app.name", defaultValue = "app")
    String name;

    @ConfigProperty(name = "app.version", defaultValue = "0.0.1")
    String version;

    @ConfigProperty(name = "app.environment", defaultValue = "local")
    String environment;


    @Operation(summary = "Application info - ATM Layer - Model")
    @APIResponses(
            value = {
                    @APIResponse(ref = "#/components/responses/InternalServerError"),
                    @APIResponse(
                            responseCode = "200",
                            description = "Success",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = InfoResponse.class)))
            })

    @GET
    @NonBlocking
    public InfoResponse info() {
        logger.info("Info environment: [{}] - name: [{}] - version: [{}]", environment, name, version);


        return InfoResponse.builder()
                .name(name)
                .version(version)
                .environment(environment)
                .description("ATM Layer - Model Service")
                .build();
    }
}
