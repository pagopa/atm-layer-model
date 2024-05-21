package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfilesRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserProfilesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class UserProfilesServiceImpl implements UserProfilesService {

    @Inject
    UserProfilesRepository userProfilesRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    UserRepository userRepository;

    @WithSession
    public Uni<Void> checkProfile(int profileId) {
        return this.profileRepository.findById(profileId)
                .onItem()
                .transformToUni(Unchecked.function(profileFound -> {
                    if (profileFound == null) {
                        throw new AtmLayerException(String.format("Non esiste un profilo con id %S", profileId), Response.Status.BAD_REQUEST, AppErrorCodeEnum.PROFILE_NOT_FOUND);
                    }
                    return Uni.createFrom().voidItem();
                }));
    }

    @WithSession
    public Uni<Void> checkUser(String userId) {
        return this.userRepository.findById(userId)
                .onItem()
                .transformToUni(Unchecked.function(userFound -> {
                    if (userFound == null) {
                        throw new AtmLayerException(String.format("Non esiste un utente con id %S", userId), Response.Status.BAD_REQUEST, AppErrorCodeEnum.NO_USER_FOUND_FOR_ID);
                    }
                    return Uni.createFrom().voidItem();
                }));
    }

    @Override
    public Uni<List<UserProfiles>> insertUserProfiles(List<UserProfiles> userProfilesList) {
        List<Uni<UserProfiles>> insertUnis = userProfilesList.stream()
                .map(this::insertSingleUserProfile)
                .toList();

        return Uni.join().all(insertUnis)
                .usingConcurrencyOf(1)
                .andCollectFailures()
                .onItem()
                .transform(list -> list);
    }

    @WithTransaction
    protected Uni<UserProfiles> insertSingleUserProfile(UserProfiles userProfiles) {
        return this.userProfilesRepository.findById(userProfiles.getUserProfilesPK())
                .onItem().transformToUni(existingUserProfile -> {
                    if (existingUserProfile != null) {
                        log.error("UserProfile for userId {} and profileId {} already exists", userProfiles.getUserProfilesPK().getUserId(), userProfiles.getUserProfilesPK().getProfileId());
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.USER_PROFILE_ALREADY_EXIST);
                    }
                    return checkProfile(userProfiles.getUserProfilesPK().getProfileId())
                            .onItem()
                            .transformToUni(isProfileVoid ->
                                    checkUser(userProfiles.getUserProfilesPK().getUserId())
                                            .onItem()
                                            .transformToUni(isUserVoid -> userProfilesRepository.persist(userProfiles)));
                });
    }

    @Override
    @WithSession
    public Uni<Optional<UserProfiles>> findById(UserProfilesPK userProfilesPK) {
        return userProfilesRepository.findById(userProfilesPK)
                .onItem()
                .transformToUni(userProfile -> Uni.createFrom().item(Optional.ofNullable(userProfile)));
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteUserProfiles(UserProfilesPK userProfilesIDs) {
        return this.userProfilesRepository.findById(userProfilesIDs)
                .onItem()
                .transformToUni(existingUserProfile -> {
                    if (existingUserProfile == null) {
                        log.error("UserProfile for userId {} and profileId {} doesn't exist", userProfilesIDs.getUserId(), userProfilesIDs.getProfileId());
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.NO_ASSOCIATION_FOUND);
                    }
                    log.info("associazione trovata {}", existingUserProfile);
                    return userProfilesRepository.delete(existingUserProfile);
                });
    }
}
