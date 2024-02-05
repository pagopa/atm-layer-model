package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfileMapper;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfileRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserProfileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.List;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.NO_USER_PROFILE_FOUND_FOR_ID;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.USER_PROFILE_WITH_SAME_ID_ALREADY_EXIST;

@ApplicationScoped
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {
    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    UserProfileMapper userProfileMapper;

    @Override
    @WithSession
    @WithTransaction
    public Uni<UserProfile> save(UserProfileCreationDto userProfile) {
        log.info("Create user {}", userProfile.getUserId());
        UserProfile localUserProfile = this.userProfileMapper.toUserProfile(userProfile);
        return this.userProfileRepository.findUserId(localUserProfile.getUserId())
                .onItem().transformToUni(Unchecked.function(x -> {
                            if(x != null){
                                throw new AtmLayerException("A user with the same id already exists",
                                        Response.Status.BAD_REQUEST,
                                        USER_PROFILE_WITH_SAME_ID_ALREADY_EXIST);
                            }
                            return this.userProfileRepository.persist(localUserProfile);
                        }
                ));
    }

    @Override
    @WithSession
    public Uni<UserProfile> findByUserId(String userId) {
        return this.userProfileRepository.findUserId(userId)
                .onItem().transformToUni(Unchecked.function(x -> {
                    if(x == null){
                        throw new AtmLayerException(String.format("A user with the id %s not exists", userId),
                                Response.Status.NOT_FOUND,
                                NO_USER_PROFILE_FOUND_FOR_ID);
                    }
                    return Uni.createFrom().item(x);
                }
                ));
    }

    @Override
    @WithSession
    public Uni<List<UserProfile>> getAll() {
        return this.userProfileRepository.findAll().list();
    }

    @Override
    @WithSession
    @WithTransaction
    public Uni<Void> delete(String userId) {
        log.info("Delete user {} from database", userId);
        return this.findByUserId(userId)
                .onItem().transformToUni(Unchecked.function(x -> userProfileRepository.delete(x)
                ));
    }

    @Override
    @WithSession
    @WithTransaction
    public Uni<UserProfile> update(UserProfileCreationDto userProfile) {
        log.info("Update user {}", userProfile.getUserId());
        return this.findByUserId(userProfile.getUserId())
                .onItem().transformToUni(Unchecked.function(x -> {
                        x.setProfile(userProfile.getProfile());
                        x.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        return userProfileRepository.persist(x);
                    }
                ));
    }
}
