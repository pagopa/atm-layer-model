package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;

public interface UserProfilesService {

    Uni<UserProfiles> insertUserProfiles(UserProfiles userProfiles);
}
