package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfilesMapper;
import it.gov.pagopa.atmlayer.service.model.service.UserProfilesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@ApplicationScoped
@Path("/user_profiles")
@Tag(name = "User Profiles")
@Slf4j
public class UserProfilesResource {

    @Inject
    UserProfilesMapper userProfilesMapper;

    @Inject
    UserProfilesService userProfilesService;

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<UserProfilesDTO>> insert(@RequestBody(required = true) @Valid UserProfilesInsertionDTO userProfilesInsertionDTO) {
        return this.userProfilesService.insertUserProfiles(userProfilesInsertionDTO)
                .onItem()
                .transform(insertedUserProfiles -> userProfilesMapper.toDtoList(insertedUserProfiles));
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<UserProfilesDTO>> update(@RequestBody(required = true) @Valid UserProfilesInsertionDTO userProfilesInsertionDTO) {
        return this.userProfilesService.updateUserProfiles(userProfilesInsertionDTO)
                .onItem()
                .transform(updatedUserProfiles -> userProfilesMapper.toDtoList(updatedUserProfiles));
    }

    @GET
    @Path("/insert-with-profiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> checkSpecificProfiles() {
        return userProfilesService.checkAtLeastTwoSpecificUserProfiles()
                .onItem()
                .transform(isAtLeastTwo -> Response.ok(isAtLeastTwo).build())
                .onFailure()
                .recoverWithItem(th -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(th.getMessage())
                        .build());
    }

    @GET
    @Path("/userId/{userId}/profileId/{profileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserProfilesDTO> getById(@PathParam("userId") String userId,
                                        @PathParam("profileId") int profileId) {
        return this.userProfilesService.getById(userId, profileId)
                .onItem()
                .transform(user -> userProfilesMapper.toDTO(user));
    }

    @DELETE
    @Path("/userId/{userId}/profileId/{profileId}")
    public Uni<Void> deleteUserProfiles(
            @PathParam("userId") String userId,
            @PathParam("profileId") int profileId
    ) {
        UserProfilesPK userProfilesPK = new UserProfilesPK(userId, profileId);
        return this.userProfilesService.deleteUserProfiles(userProfilesPK);
    }


}
