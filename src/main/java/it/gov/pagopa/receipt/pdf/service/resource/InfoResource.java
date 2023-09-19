package it.gov.pagopa.receipt.pdf.service.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.Cancellable;
import it.gov.pagopa.receipt.pdf.service.model.DemoValidation;
import it.gov.pagopa.receipt.pdf.service.model.InfoResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
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
@Tag(name = "Info", description = "Info operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InfoResource {

    private final Logger logger = LoggerFactory.getLogger(InfoResource.class);

    @ConfigProperty(name = "app.name", defaultValue = "app")
    String name;

    @ConfigProperty(name = "app.version", defaultValue = "0.0.0")
    String version;

    @ConfigProperty(name = "app.environment", defaultValue = "local")
    String environment;

    @Operation(summary = "Get info of Receipt PDF Service")
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
    public InfoResponse info() throws InterruptedException {
        logger.info("Info environment: [{}] - name: [{}] - version: [{}]", environment, name, version);


        return InfoResponse.builder()
                .name(name)
                .version(version)
                .environment(environment)
                .description("Receipt PDF Service")
                .build();
    };

    @POST
    public InfoResponse demoValidation(@Valid DemoValidation example) {
        logger.info("Info environment: [{}] - name: [{}] - version: [{}]", environment, name, version);

        return InfoResponse.builder()
                .name(name)
                .version(version)
                .environment(environment)
                .description("Receipt PDF Service")
                .build();
    }
}
