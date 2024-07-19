package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfilesMapper;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfilesRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserProfilesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
@Slf4j
public class UserProfilesServiceImpl implements UserProfilesService {

    @Inject
    UserProfilesRepository userProfilesRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    UserProfilesMapper userProfilesMapper;

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
    public Uni<List<Void>> checkProfileList(List<Integer> listInt) {
        List<Uni<Void>> checks = listInt.stream()
                .map(this::checkProfile)
                .toList();

        return Uni.join().all(checks)
                .usingConcurrencyOf(1)
                .andFailFast();
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
    public Uni<List<UserProfiles>> insertUserProfiles(UserProfilesInsertionDTO userProfilesInsertionDTO) {
        List<UserProfiles> userProfilesList = userProfilesMapper.toEntityInsertion(userProfilesInsertionDTO);
        List<Uni<Boolean>> checks = userProfilesList.stream()
                .map(this::isUserProfileExisting)
                .toList();

        return Uni.join().all(checks)
                .andCollectFailures()
                .onItem()
                .transform(existingFlags -> {
                    boolean anyProfileAlreadyExists = existingFlags.stream().anyMatch(exists -> exists);
                    if (anyProfileAlreadyExists) {
                        throw new AtmLayerException(Response.Status.BAD_REQUEST, AppErrorCodeEnum.USER_PROFILE_ALREADY_EXIST);
                    }
                    return userProfilesList;
                })
                .onItem()
                .transformToUni(profilesToInsert -> {
                    List<Uni<UserProfiles>> insertUnis = profilesToInsert.stream()
                            .map(this::insertSingleUserProfile)
                            .toList();

                    return Uni.join().all(insertUnis)
                            .usingConcurrencyOf(1)
                            .andCollectFailures()
                            .onItem()
                            .transform(list -> list);
                });
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

    @WithTransaction
    protected Uni<Boolean> isUserProfileExisting(UserProfiles userProfiles) {
        return this.userProfilesRepository.findById(userProfiles.getUserProfilesPK())
                .onItem().transform(Objects::nonNull);
    }

    @Override
    @WithSession
    public Uni<UserProfiles> getById(String userId, int profileId) {
        UserProfilesPK userProfilesPK = new UserProfilesPK(userId, profileId);
        return userProfilesRepository.findById(userProfilesPK)
                .onItem()
                .transform(userProfile -> {
                    if (userProfile == null) {
                        throw new AtmLayerException(Response.Status.NOT_FOUND, AppErrorCodeEnum.NO_ASSOCIATION_FOUND);
                    }
                    return userProfile;
                });
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

    @WithTransaction
    public Uni<List<UserProfiles>> updateUserProfiles(UserProfilesInsertionDTO userProfilesInsertionDTO) {
        List<UserProfiles> userProfilesToUpdate = userProfilesMapper.toEntityInsertion(userProfilesInsertionDTO);

        return this.checkUser(userProfilesInsertionDTO.getUserId())
                .onItem()
                .transformToUni(checkedUser -> checkProfileList(userProfilesInsertionDTO.getProfileIds())
                        .onItem()
                        .transformToUni(checkedProfiles -> this.userProfilesRepository.findByUserId(userProfilesInsertionDTO.getUserId())
                                .onItem()
                                .transformToUni(userProfilesSaved -> {
                                    List<Integer> userProfilesSavedIds = userProfilesSaved.stream().map(x -> x.getUserProfilesPK().getProfileId()).toList();
                                    List<Integer> userProfilesToUpdateIds = userProfilesToUpdate.stream().map(y -> y.getUserProfilesPK().getProfileId()).toList();
                                    List<UserProfiles> userProfilesToDelete = userProfilesSaved.stream().filter(w -> !userProfilesToUpdateIds.contains(w.getUserProfilesPK().getProfileId())).toList();
                                    List<UserProfiles> userProfilesToAdd = userProfilesToUpdate.stream().filter(j -> !userProfilesSavedIds.contains(j.getUserProfilesPK().getProfileId())).toList();
                                    return userProfilesRepository.deleteUserProfiles(userProfilesToDelete.stream().map(UserProfiles::getUserProfilesPK).toList())
                                            .onItem()
                                            .transformToUni(deletedRows -> userProfilesRepository.persist(userProfilesToAdd))
                                            .onItem()
                                            .transformToUni(persistedRows -> userProfilesRepository.findByUserId(userProfilesInsertionDTO.getUserId()));
                                })
                        )
                );
    }

    @WithSession
    public Uni<Void> checkAtLeastTwoSpecificUserProfiles() {
        return hasAtLeastTwoSpecificUserProfiles()
                .onItem()
                .transform(isAtLeastTwo -> {
                    if (!isAtLeastTwo) {
                        throw new AtmLayerException("Meno di due occorrenze trovate per il profilo specificato.", Response.Status.BAD_REQUEST, AppErrorCodeEnum.NO_ASSOCIATION_FOUND);
                    }
                    return null;
                });
    }

    @WithSession
    public Uni<Boolean> hasAtLeastTwoSpecificUserProfiles() {
        return userProfilesRepository.findUserProfilesWithSpecificProfile()
                .onItem()
                .transform(userProfiles -> userProfiles.size() >= 2);
    }

}
