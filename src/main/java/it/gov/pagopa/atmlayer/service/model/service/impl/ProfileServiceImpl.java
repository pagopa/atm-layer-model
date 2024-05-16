package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.ProfileMapper;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import it.gov.pagopa.atmlayer.service.model.service.ProfileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ProfileServiceImpl implements ProfileService {

    @Inject
    ProfileMapper profileMapper;

    @Inject
    ProfileRepository profileRepository;

    @WithSession
    public Uni<Boolean> checkUnique(int profileId) {
        return this.profileRepository.findById(profileId)
                .onItem()
                .transform(foundedProfile -> {
                    if (foundedProfile != null) {
                        throw new AtmLayerException(String.format("Esiste già un profilo con id %S", profileId), Response.Status.BAD_REQUEST, AppErrorCodeEnum.PROFILE_ALREADY_EXIST);
                    }
                    return true;
                });
    }

    @WithSession
    public Uni<Profile> checkProfileId(int profileId) {
        return this.profileRepository.findById(profileId)
                .onItem()
                .transform(id -> {
                    if (id == null) {
                        throw new AtmLayerException(String.format("Non esiste un profilo con id %S", profileId), Response.Status.BAD_REQUEST, AppErrorCodeEnum.PROFILE_NOT_FOUND);
                    }
                    return id;
                });
    }

    @Override
    @WithTransaction
    public Uni<Profile> createProfile(ProfileCreationDto profileDto) {
        Profile newProfile = this.profileMapper.toEntity(profileDto);
        return checkUnique(profileDto.getProfileId())
                .onItem()
                .transformToUni(isUnique -> this.profileRepository.persist(newProfile));
    }

    @Override
    @WithSession
    public Uni<Profile> retrieveProfile(int profileId) {
        return checkProfileId(profileId);
    }

    @Override
    @WithTransaction
    public Uni<Profile> updateProfile(ProfileCreationDto profile) {

        return checkProfileId(profile.getProfileId())
                .onItem()
                .transformToUni(updateProfile -> {
                    updateProfile.setDescription(profile.getDescription());
                    return this.profileRepository.persist(updateProfile);
                });
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteProfile(int profileId) {
        return checkProfileId(profileId)
                .onItem()
                .transformToUni(profileToDelete -> this.profileRepository.delete(profileToDelete));
    }
}
