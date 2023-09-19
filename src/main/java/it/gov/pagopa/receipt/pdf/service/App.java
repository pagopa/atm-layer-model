package it.gov.pagopa.receipt.pdf.service;

import io.quarkus.runtime.Startup;
import it.gov.pagopa.receipt.pdf.service.model.ErrorResponse;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
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
                                     "instance": "PDFS_603"
                                   }""")),
              @APIResponse(
                  name = "AppException400",
                  responseCode = "400",
                  description = "Default app exception for status 400",
                  content =
                      @Content(
                          mediaType = MediaType.APPLICATION_JSON,
                          schema = @Schema(implementation = ErrorResponse.class),
                          examples =
                            @ExampleObject(
                                name = "Error",
                                value =
                                    """
                                  {
                                     "type": "",
                                     "title": "Bad Request",
                                     "status": 400,
                                     "detail": "The provided third party id [<td_id>] is invalid",
                                     "instance": "PDFS_703"
                                   }"""))),
              @APIResponse(
                  name = "AppException404",
                  responseCode = "404",
                  description = "Default app exception for status 404",
                  content =
                      @Content(
                          mediaType = MediaType.APPLICATION_JSON,
                          schema = @Schema(implementation = ErrorResponse.class),
                          example =
                              """
                                  {
                                     "type": "",
                                     "title": "Not Found",
                                     "status": 404,
                                     "detail": "Third party id [<td_id>] not found",
                                     "instance": "PDFS_900"
                                   }""")),
            }),
    info = @Info(title = "Receipt PDF service", version = "0.0.0-SNAPSHOT"))
@ApplicationPath("/api")
@Startup
public class App extends Application {}
