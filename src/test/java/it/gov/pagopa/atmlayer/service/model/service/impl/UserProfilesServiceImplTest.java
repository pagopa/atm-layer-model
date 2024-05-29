package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfilesRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserProfilesServiceImplTest {
    @Mock
    UserProfilesRepository userProfilesRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserProfilesServiceImpl userProfilesService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckProfile() {
        int profileId = 1;
        Profile profile = new Profile();

        when(profileRepository.findById(profileId)).thenReturn(Uni.createFrom().item(profile));

        userProfilesService.checkProfile(profileId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(profileRepository).findById(profileId);
    }

    @Test
    void testCheckProfileExceptionCase() {
        int profileId = 2;

        when(profileRepository.findById(profileId)).thenReturn(Uni.createFrom().nullItem());

        userProfilesService.checkProfile(profileId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailed()
                .assertFailedWith(AtmLayerException.class, String.format("Non esiste un profilo con id %S", profileId));

        verify(profileRepository).findById(profileId);
    }

    @Test
    void testCheckUser() {
        String userId = "existingUserId";
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Uni.createFrom().item(user));

        userProfilesService.checkUser(userId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(userRepository).findById(userId);
    }

    @Test
    void testCheckUserExceptionCase() {
        String userId = "nonExistingUserId";

        when(userRepository.findById(userId)).thenReturn(Uni.createFrom().nullItem());

        userProfilesService.checkUser(userId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailed()
                .assertFailedWith(AtmLayerException.class, String.format("Non esiste un utente con id %S", userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void testInsertSingleUserProfileOK() {
        UserProfilesPK pk = new UserProfilesPK("testUserId", 1);
        UserProfiles userProfiles = new UserProfiles();
        userProfiles.setUserProfilesPK(pk);

        when(userProfilesRepository.findById(pk)).thenReturn(Uni.createFrom().nullItem());
        when(userProfilesRepository.persist(any(UserProfiles.class))).thenReturn(Uni.createFrom().item(userProfiles));
        when(profileRepository.findById(pk.getProfileId())).thenReturn(Uni.createFrom().item(new Profile()));
        when(userRepository.findById(pk.getUserId())).thenReturn(Uni.createFrom().item(new User()));

        userProfilesService.insertSingleUserProfile(userProfiles)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(userProfiles);

        verify(userProfilesRepository, times(1)).findById(pk);
        verify(userProfilesRepository, times(1)).persist(userProfiles);
        verify(profileRepository, times(1)).findById(pk.getProfileId());
        verify(userRepository, times(1)).findById(pk.getUserId());
    }

    @Test
    void testInsertSingleUserProfileAlreadyExists() {
        UserProfilesPK pk = new UserProfilesPK("testUserId", 1);
        UserProfiles existingUserProfiles = new UserProfiles();
        existingUserProfiles.setUserProfilesPK(pk);

        when(userProfilesRepository.findById(pk)).thenReturn(Uni.createFrom().item(existingUserProfiles));

        UserProfiles newUserProfiles = new UserProfiles();
        newUserProfiles.setUserProfilesPK(pk);

        userProfilesService.insertSingleUserProfile(newUserProfiles)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);

        verify(userProfilesRepository, times(1)).findById(pk);
        verify(userProfilesRepository, times(0)).persist(any(UserProfiles.class));
        verify(profileRepository, times(0)).findById(anyInt());
        verify(userRepository, times(0)).findById(anyString());
    }

}
