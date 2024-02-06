package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileService {

    Uni<UserProfile> createUser(UserProfileCreationDto userProfile);
    Uni<UserProfile> findByUserId(String userId);
    Uni<List<UserProfile>> getUsers();
    Uni<Void> deleteUser(String userId);
    Uni<UserProfile> updateUser(UserProfileCreationDto userProfile);
}
