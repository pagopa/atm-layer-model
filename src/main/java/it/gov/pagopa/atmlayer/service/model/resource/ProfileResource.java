package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.mapper.ProfileMapper;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import it.gov.pagopa.atmlayer.service.model.service.ProfileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
    public Uni<ProfileDTO> createProfile(ProfileCreationDto profile) {
        return this.profileService.createProfile(profile)
                .onItem()
                .transform(savedProfile -> profileMapper.toDto(savedProfile));
    }
}

