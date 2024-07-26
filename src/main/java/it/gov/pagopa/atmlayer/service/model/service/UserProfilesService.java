package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;

import java.util.List;

public interface UserProfilesService {

    Uni<List<UserProfiles>> insertUserProfiles(UserProfilesInsertionDTO userProfilesInsertionDTO);

    Uni<UserProfiles> getById(String userId, int profileId);

    Uni<Void> deleteUserProfiles(UserProfilesPK userProfilesIDs);

    Uni<List<UserProfiles>> updateUserProfiles(UserProfilesInsertionDTO userProfilesInsertionDTO);

    Uni<Void> checkAtLeastTwoSpecificUserProfiles();
}
