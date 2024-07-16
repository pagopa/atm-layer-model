package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileAllDto;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileDto;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfileMapper;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.UserProfileService;
import it.gov.pagopa.atmlayer.service.model.validators.UserProfileValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@ApplicationScoped
@Path("/users")
@Tag(name = "USERS", description = "USERS operations")
@Slf4j
public class UserProfileResource {


    private final UserProfileService userProfileService;
    private final UserProfileMapper userProfileMapper;
    private final UserProfileValidator userProfileValidator;

    @Inject
    public UserProfileResource(UserProfileService userProfileService, UserProfileMapper userProfileMapper,
                               UserProfileValidator userProfileValidator){
        this.userProfileService = userProfileService;
        this.userProfileMapper = userProfileMapper;
        this.userProfileValidator = userProfileValidator;
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "findByUserId",
            description = "cerca per userId"
    )
    @APIResponse(responseCode= "200", description = "Ok", content = @Content(schema = @Schema(implementation = UserProfileDto.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}" ))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}" ))
    public Uni<UserProfileDto> findByUserId(@NotNull @QueryParam("userId") @Schema(format = "byte", maxLength = 255) String userId) {
        return this.userProfileService.findByUserId(userId)
                .onItem()
                .transformToUni(Unchecked.function( x -> {
                    UserProfileDto userProfileDto = this.userProfileMapper.toUserProfileDtoWithProfileMapping(x);
                    return Uni.createFrom().item(userProfileDto);
                }));
    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @APIResponses(value = {
//            @APIResponse(responseCode = "200", description = "Successfully retrieved"),
//            @APIResponse(responseCode = "404", description = "Not found")
//    })
//    public Uni<List<UserProfileAllDto>> getUsers(){
//        return this.userProfileService.getUsers()
//                .onItem()
//                .transform(Unchecked.function(list -> {
//                    if (list.isEmpty()) {
//                        log.info("No User profiles saved in database");
//                    }
//                    return userProfileMapper.toDtoAllList(list);
//                }));
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    @Operation(
            operationId = "createUser",
            description = "creazione user"
    )
    @APIResponse(responseCode= "200", description = "Ok", content = @Content(schema = @Schema(implementation = UserProfileAllDto.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}" ))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}" ))
    public Uni<UserProfileAllDto> createUser(
            @RequestBody(required = true, content = {
                    @Content(schema = @Schema(implementation = UserProfileCreationDto.class))
            }) @Valid UserProfileCreationDto user){
        return this.userProfileValidator.validateExistenceProfileType(user.getProfile())
                .onItem()
                .transformToUni(x -> this.userProfileService.createUser(user)
                         .onItem()
                         .transformToUni(Unchecked.function(u -> Uni.createFrom().item(this.userProfileMapper.toUserProfileAllDto(u)))));
    }

    @DELETE
    @Path("/search")
    @Operation(
            operationId = "deleteUser",
            description = "elimina user"
    )
    @APIResponse(responseCode= "204", description = "Ok")
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}" ))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}" ))
    public Uni<Void> deleteUser(@NotNull @QueryParam("userId") @Schema(format = "byte", maxLength = 255) String userId) {
        return this.userProfileService.deleteUser(userId);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "updateUser",
            description = "aggiorna user"
    )
    @APIResponse(responseCode= "200", description = "Ok", content = @Content(schema = @Schema(implementation = UserProfileAllDto.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}" ))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}" ))
    public Uni<UserProfileAllDto> updateUser(
            @RequestBody(required = true) @Valid UserProfileCreationDto user) {
        return this.userProfileValidator.validateExistenceProfileType(user.getProfile())
                .onItem()
                .transformToUni(x -> this.userProfileService.updateUser(user)
                        .onItem()
                        .transformToUni(Unchecked.function(u -> Uni.createFrom().item(this.userProfileMapper.toUserProfileAllDto(u)))));
    }
}
