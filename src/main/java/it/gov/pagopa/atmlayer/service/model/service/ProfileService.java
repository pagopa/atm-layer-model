package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;

import java.util.List;

public interface ProfileService {
    Uni<Profile> createProfile(ProfileCreationDto profile);

    Uni<Profile> retrieveProfile(int profileId);

    Uni<Profile> updateProfile(ProfileCreationDto profile);

    Uni<Void> deleteProfile(int profileId);

    Uni<List<Profile>> getAll();
}
