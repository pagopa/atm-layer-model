package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
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
        List<UserProfiles> userProfilesList = userProfilesMapper.toEntityInsertion(userProfilesInsertionDTO);
        return this.userProfilesService.insertUserProfiles(userProfilesList)
                .onItem()
                .transform(insertedUserProfiles -> insertedUserProfiles.stream()
                        .map(userProfilesMapper::toDTO)
                        .toList());
    }

    @GET
    @Path("/userId/{userId}/profileId/{profileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserProfilesDTO> getById(@PathParam("userId") String userId,
                                        @PathParam("profileId") int profileId) {
        UserProfilesPK userProfilesPK = new UserProfilesPK(userId, profileId);
        return this.userProfilesService.findById(userProfilesPK)
                .onItem()
                .transform(Unchecked.function(x -> {
                    if (x.isEmpty()) {
                        throw new AtmLayerException(Response.Status.NOT_FOUND, AppErrorCodeEnum.NO_USER_PROFILE_FOUND);
                    }
                    return userProfilesMapper.toDTO(x.get());
                }));
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
