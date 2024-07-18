package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.ProfileMapperImpl;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@QuarkusTest
class ProfileServiceImplTest {
    @Mock
    ProfileRepository profileRepository;
    @Mock
    ProfileMapperImpl profileMapper;
    @InjectMocks
    ProfileServiceImpl service;

    ProfileCreationDto profileCreationDto = new ProfileCreationDto();
    Profile profile = new Profile();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        profileCreationDto.setProfileId(1);
        profileCreationDto.setDescription("1");
        profile.setProfileId(1);
        profile.setDescription("1");
    }

    @Test
    void createProfileTestOK() {
        when(profileMapper.toEntity(any(ProfileCreationDto.class))).thenReturn(profile);
        when(profileRepository.persist(profile)).thenReturn(Uni.createFrom().item(profile));

        service.createProfile(profileCreationDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(profile);

        verify(profileMapper).toEntity(profileCreationDto);
        verify(profileRepository).persist(profile);
    }

    @Test
    void createProfileTestKO() {
        when(profileMapper.toEntity(any(ProfileCreationDto.class))).thenReturn(profile);
        when(profileRepository.findById(1)).thenReturn(Uni.createFrom().item(profile));

        service.createProfile(profileCreationDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Esiste già un profilo con id 1");

        verify(profileMapper).toEntity(profileCreationDto);
        verify(profileRepository).findById(1);
    }

    @Test
    void retriveProfileTestOK() {
        when(profileRepository.findById(1)).thenReturn(Uni.createFrom().item(profile));

        service.retrieveProfile(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(profile);
    }

    @Test
    void retriveProfileTestKO() {
        when(profileRepository.findById(1)).thenReturn(Uni.createFrom().nullItem());

        service.retrieveProfile(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Non esiste un profilo con id 1");
    }

    @Test
    void updateProfileTestOK() {
        when(profileRepository.findById(1)).thenReturn(Uni.createFrom().item(profile));
        profile.setDescription("1.1");
        when(profileRepository.persist(profile)).thenReturn(Uni.createFrom().item(profile));

        service.updateProfile(profileCreationDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(profile);

        verify(profileRepository).findById(1);
        verify(profileRepository).persist(profile);
    }

    @Test
    void updateProfileTestKO() {
        when(profileRepository.findById(1)).thenReturn(Uni.createFrom().nullItem());
        profile.setDescription("1.1");
        when(profileRepository.persist(profile)).thenReturn(Uni.createFrom().item(profile));

        service.updateProfile(profileCreationDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Non esiste un profilo con id 1");
    }

    @Test
    void deleteProfileOK() {
        when(profileRepository.findById(1)).thenReturn(Uni.createFrom().item(profile));
        when(profileRepository.delete(profile)).thenReturn(Uni.createFrom().voidItem());

        service.deleteProfile(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(profileRepository).findById(1);
        verify(profileRepository).delete(profile);
    }

    @Test
    void deleteProfileKO() {
        when(profileRepository.findById(1)).thenReturn(Uni.createFrom().nullItem());
        when(profileRepository.delete(profile)).thenReturn(Uni.createFrom().voidItem());

        service.deleteProfile(1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Non esiste un profilo con id 1");
    }

    @Test
    void getAllTestOK() {
        List<Profile> listProfile = Arrays.asList(profile, new Profile());
        PanacheQuery<Profile> query = mock(PanacheQuery.class);

        when(profileRepository.findAll()).thenReturn(query);
        when(query.list()).thenReturn(Uni.createFrom().item(listProfile));

        service.getAll()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(listProfile);

        verify(profileRepository).findAll();
        verify(query).list();
    }
}
