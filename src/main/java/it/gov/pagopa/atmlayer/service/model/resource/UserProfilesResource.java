package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfilesMapper;
import it.gov.pagopa.atmlayer.service.model.service.UserProfilesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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
    public Uni<UserProfilesDTO> insert(@RequestBody(required = true) @Valid UserProfilesInsertionDTO userProfilesInsertionDTO) {
        UserProfiles userProfiles = userProfilesMapper.toEntityInsertion(userProfilesInsertionDTO);
        return this.userProfilesService.insertUserProfiles(userProfiles)
                .onItem()
                .transformToUni(insertedUserProfile -> Uni.createFrom().item(userProfilesMapper.toDTO(insertedUserProfile)));
    }
}
