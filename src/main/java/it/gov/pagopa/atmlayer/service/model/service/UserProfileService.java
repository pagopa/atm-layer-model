package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileService {

    Uni<UserProfile> save(UserProfileCreationDto userProfile);
    Uni<UserProfile> findByUserId(String userId);
    Uni<List<UserProfile>> getAll();
    Uni<Void> delete(String userId);
    Uni<UserProfile> update(UserProfileCreationDto userProfile);
}
