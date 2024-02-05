package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileDto;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfileMapper;
import it.gov.pagopa.atmlayer.service.model.service.UserProfileService;
import it.gov.pagopa.atmlayer.service.model.validators.UserProfileValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@ApplicationScoped
@Path("/users")
@Tag(name = "USERS", description = "USERS operations")
@Slf4j
public class UserProfileResource {

    @Inject
    UserProfileService userProfileService;
    @Inject
    UserProfileMapper userProfileMapper;
    @Inject
    UserProfileValidator userProfileValidator;

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserProfileDto> findByUserId(@NotNull @QueryParam("userId") String userId) {
        return this.userProfileService.findByUserId(userId)
                .onItem()
                .transformToUni(Unchecked.function( x -> {
                    UserProfileDto userProfileDto = this.userProfileMapper.toUserProfileDtoWithProfileMapping(x);
                    return Uni.createFrom().item(userProfileDto);
                }));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<UserProfileDto>> getAll(){
        return this.userProfileService.getAll()
                .onItem()
                .transform(Unchecked.function(list -> {
                    if (list.isEmpty()) {
                        log.info("No User profiles saved in database");
                    }
                    return list.stream()
                            .map( x -> this.userProfileMapper.toUserProfileDto(x))
                            .toList();
                }));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserProfileDto> create(
            @RequestBody(required = true) @Valid UserProfileCreationDto user) throws IOException {
        return this.userProfileValidator.validateExistenceProfileType(user.getProfile())
                .onItem()
                .transformToUni((x) -> this.userProfileService.save(user)
                         .onItem()
                         .transformToUni(Unchecked.function(u -> Uni.createFrom().item(this.userProfileMapper.toUserProfileDto(u)))));
    }

    @DELETE
    @Path("/search")
    public Uni<Void> delete(@NotNull @QueryParam("userId") String userId) {
        return this.userProfileService.delete(userId);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserProfileDto> update(
            @RequestBody(required = true) @Valid UserProfileCreationDto user) throws NoSuchAlgorithmException, IOException {
        return this.userProfileValidator.validateExistenceProfileType(user.getProfile())
                .onItem()
                .transformToUni((x) -> this.userProfileService.update(user)
                        .onItem()
                        .transformToUni(Unchecked.function(u -> Uni.createFrom().item(this.userProfileMapper.toUserProfileDto(u)))));
    }
}
