package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfilesRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserProfilesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@ApplicationScoped
@Slf4j
public class UserProfilesServiceImpl implements UserProfilesService {

    @Inject
    UserProfilesRepository userProfilesRepository;

    @Override
    @WithTransaction
    public Uni<UserProfiles> insertUserProfiles(UserProfiles userProfiles) {
        log.info("Inserting userProfiles with userId : {} and profileId : {}", userProfiles.getUserProfilesPK().getUserId(), userProfiles.getUserProfilesPK().getProfileId());
        return this.userProfilesRepository.findById(userProfiles.getUserProfilesPK())
                .onItem()
                .transformToUni(existingUserProfile -> {
                    if (existingUserProfile != null) {
                        log.error("UserProfile for userId {} and profileId {} already exists", userProfiles.getUserProfilesPK().getUserId(), userProfiles.getUserProfilesPK().getProfileId());
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.USER_PROFILE_ALREADY_EXIST);
                    }
                    return userProfilesRepository.persist(userProfiles);
                });
    }

    @Override
    @WithSession
    public Uni<Optional<UserProfiles>> findById(UserProfilesPK userProfilesPK) {
        return userProfilesRepository.findById(userProfilesPK)
                .onItem()
                .transformToUni(userProfile -> Uni.createFrom().item(Optional.ofNullable(userProfile)));
    }
}
