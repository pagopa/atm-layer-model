package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;

import java.util.Optional;

public interface UserProfilesService {

    Uni<UserProfiles> insertUserProfiles(UserProfiles userProfiles);

    Uni<Optional<UserProfiles>> findById(UserProfilesPK userProfilesPK);
}
