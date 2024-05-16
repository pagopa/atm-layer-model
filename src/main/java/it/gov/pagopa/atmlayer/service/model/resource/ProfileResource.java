package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.mapper.ProfileMapper;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import it.gov.pagopa.atmlayer.service.model.service.ProfileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/profile")
public class ProfileResource {

    @Inject
    ProfileService profileService;
    @Inject
    ProfileMapper profileMapper;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ProfileDTO> createProfile(@Valid ProfileCreationDto profile) {
        return this.profileService.createProfile(profile)
                .onItem()
                .transform(savedProfile -> profileMapper.toDto(savedProfile));
    }

    @GET
    @Path("/{profileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ProfileDTO> retrieveProfile(@PathParam("profileId") int profileId) {
        return this.profileService.retrieveProfile(profileId)
                .onItem()
                .transform(retrievedProfile -> profileMapper.toDto(retrievedProfile));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ProfileDTO> updateProfile(@Valid ProfileCreationDto profile) {
        return this.profileService.updateProfile(profile)
                .onItem()
                .transform(updatedProfile -> profileMapper.toDto(updatedProfile));
    }

    @DELETE
    @Path("/{profileId}")
    public Uni<Void> deleteProfile(@PathParam("profileId") int profileId) {
        return this.profileService.deleteProfile(profileId);
    }
}

