package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;

import java.util.List;
import java.util.Optional;

public interface UserProfilesService {

    Uni<List<UserProfiles>> insertUserProfiles(List<UserProfiles> userProfilesList);

    Uni<Optional<UserProfiles>> findById(UserProfilesPK userProfilesPK);
}
