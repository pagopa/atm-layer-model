package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfilesMapper;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfilesRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserProfilesServiceImplTest {
    @Mock
    UserProfilesRepository userProfilesRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    UserProfilesMapper userProfilesMapper;
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
    void testCheckProfileList_Success() {
        List<Integer> listInt = Arrays.asList(1, 2, 3);

        when(profileRepository.findById(anyInt()))
                .thenReturn(Uni.createFrom().item(new Profile()));

        Uni<List<Void>> resultUni = userProfilesService.checkProfileList(listInt);

        CompletionStage<List<Void>> completionStage = resultUni.subscribe().asCompletionStage();
        List<Void> resultList = completionStage.toCompletableFuture().join();

        assertThat(resultList, hasSize(listInt.size()));

        for (Integer id : listInt) {
            verify(profileRepository).findById(id);
        }
    }

    @Test
    void testCheckProfileList_ProfileNotFound() {
        List<Integer> listInt = Arrays.asList(1, 2, 3);

        when(profileRepository.findById(anyInt()))
                .thenReturn(Uni.createFrom().nullItem());

        Uni<List<Void>> resultUni = userProfilesService.checkProfileList(listInt);

        resultUni
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailed();

        for (Integer id : listInt) {
            verify(profileRepository).findById(id);
        }
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

    @Test
    void testIsUserProfileExisting_existingProfile() {
        UserProfilesPK userProfilesPK = new UserProfilesPK("userId", 1);
        UserProfiles existingUserProfile = new UserProfiles(userProfilesPK);
        when(userProfilesRepository.findById(userProfilesPK)).thenReturn(Uni.createFrom().item(existingUserProfile));

        Uni<Boolean> result = userProfilesService.isUserProfileExisting(existingUserProfile);

        assertTrue(result.await().indefinitely());

        verify(userProfilesRepository, times(1)).findById(userProfilesPK);
    }

    @Test
    void testIsUserProfileExisting_nonExistingProfile() {
        UserProfilesPK userProfilesPK = new UserProfilesPK("userId", 1);
        when(userProfilesRepository.findById(userProfilesPK)).thenReturn(Uni.createFrom().nullItem());

        Uni<Boolean> result = userProfilesService.isUserProfileExisting(new UserProfiles(userProfilesPK));

        assertFalse(result.await().indefinitely());

        verify(userProfilesRepository, times(1)).findById(userProfilesPK);
    }

    @Test
    void testDeleteUserProfiles_profileFound() {
        UserProfilesPK userProfilesIDs = new UserProfilesPK("userId", 1);
        UserProfiles existingUserProfile = new UserProfiles();
        when(userProfilesRepository.findById(any(UserProfilesPK.class))).thenReturn(Uni.createFrom().item(existingUserProfile));
        when(userProfilesRepository.delete(existingUserProfile)).thenReturn(Uni.createFrom().nullItem());

        Uni<Void> result = userProfilesService.deleteUserProfiles(userProfilesIDs);

        result.await().indefinitely();

        verify(userProfilesRepository, times(1)).findById(userProfilesIDs);
        verify(userProfilesRepository, times(1)).delete(existingUserProfile);
    }

    @Test
    void testDeleteUserProfiles_profileNotFound() {
        UserProfilesPK userProfilesIDs = new UserProfilesPK("userId", 1);
        when(userProfilesRepository.findById(any(UserProfilesPK.class))).thenReturn(Uni.createFrom().nullItem());

        Uni<Void> result = userProfilesService.deleteUserProfiles(userProfilesIDs);

        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> result.await().indefinitely());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getStatusCode());
        assertEquals(AppErrorCodeEnum.NO_ASSOCIATION_FOUND.getErrorMessage(), exception.getMessage());

        verify(userProfilesRepository, times(1)).findById(userProfilesIDs);
    }


    @Test
    void testUpdateUserProfiles_UserNotFound() {
        String userId = "1";
        UserProfilesInsertionDTO userProfilesInsertionDTO = new UserProfilesInsertionDTO();
        userProfilesInsertionDTO.setUserId(userId);
        userProfilesInsertionDTO.setProfileIds(Arrays.asList(1, 2, 3));

        when(userProfilesService.checkUser(userId)).thenReturn(Uni.createFrom().failure(new RuntimeException("User not found")));

        Uni<List<UserProfiles>> resultUni = userProfilesService.updateUserProfiles(userProfilesInsertionDTO);

        assertThrows(Exception.class, () -> resultUni.await().indefinitely());
        verify(userProfilesRepository, never()).findByUserId(anyString());
    }

    /*@Test
    void testUpdateUserProfiles_Success() {
        String userId = "1";
        UserProfilesInsertionDTO userProfilesInsertionDTO = new UserProfilesInsertionDTO();
        userProfilesInsertionDTO.setUserId(userId);
        userProfilesInsertionDTO.setProfileIds(Arrays.asList(1, 2, 3));

        List<UserProfiles> userProfilesToUpdate = Arrays.asList(new UserProfiles(), new UserProfiles());

        when(userProfilesRepository.findByUserId(userId)).thenReturn(Uni.createFrom().item(List.of(new UserProfiles())));
        when(userProfilesMapper.toEntityInsertion(userProfilesInsertionDTO)).thenReturn(userProfilesToUpdate);
        when(userProfilesRepository.deleteUserProfiles(any())).thenReturn(Uni.createFrom().item(1L));
        doAnswer(invocation -> Uni.createFrom().nullItem()).when(userProfilesRepository).persist((UserProfiles) any());

        Uni<List<UserProfiles>> resultUni = userProfilesService.updateUserProfiles(userProfilesInsertionDTO);

        verify(userProfilesRepository).findByUserId(userProfilesInsertionDTO.getUserId());
        verify(userProfilesRepository).deleteUserProfiles(any());
        verify(userProfilesRepository).persist((UserProfiles) any());

        resultUni
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(List.of(new UserProfiles()));
    }*/
}
