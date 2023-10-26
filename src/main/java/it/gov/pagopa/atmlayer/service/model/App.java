package it.gov.pagopa.atmlayer.service.model;

import io.quarkus.runtime.Startup;
import it.gov.pagopa.atmlayer.service.model.model.ErrorResponse;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@OpenAPIDefinition(
        components =
        @Components(
                responses = {
                        @APIResponse(
                                name = "InternalServerError",
                                responseCode = "500",
                                description = "Internal Server Error",
                                content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON,
                                        schema = @Schema(implementation = ErrorResponse.class),
                                        example =
                                                """
                                                        {
                                                           "type": "",
                                                           "title": "Internal Server Error",
                                                           "status": 500,
                                                           "detail": "An unexpected error has occurred. Please contact support.",
                                                           "instance": "ATMLM_500"
                                                         }""")),

                }),
        info = @Info(title = "ATM Layer - Model service", version = "0.0.1-SNAPSHOT"))
@Startup
public class App extends Application {
}
