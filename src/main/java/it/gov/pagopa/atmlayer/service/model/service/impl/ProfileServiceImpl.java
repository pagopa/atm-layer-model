package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.mapper.ProfileMapper;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import it.gov.pagopa.atmlayer.service.model.service.ProfileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProfileServiceImpl implements ProfileService {

    @Inject
    ProfileMapper profileMapper;

    @Inject
    ProfileRepository profileRepository;

    @Override
    public Uni<Profile> createProfile(ProfileCreationDto profileDto) {
        Profile newProfile = this.profileMapper.toEntity(profileDto);
        return this.profileRepository.persist(newProfile);
    };
}
