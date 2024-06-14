package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfilesMapper;
import it.gov.pagopa.atmlayer.service.model.repository.ProfileRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfilesRepository;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserProfilesServiceImplTest {
    @Mock
    ProfileRepository profileRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserProfilesRepository userProfilesRepository;
    @Mock
    UserProfilesMapper userProfilesMapper;
    @InjectMocks
    UserProfilesServiceImpl userProfilesService;
    private UserProfilesPK userProfilesPK;
    private UserProfiles userProfiles;
    private UserProfilesInsertionDTO userProfilesInsertionDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userProfilesPK = new UserProfilesPK("prova@test.com", 2);
        userProfiles = new UserProfiles();
        userProfiles.setUserProfilesPK(userProfilesPK);
        userProfiles.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userProfiles.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

        User user = new User();
        user.setUserId(userProfilesPK.getUserId());
        user.setName("prova");
        user.setSurname("test");

        userProfiles.setUser(user);

        List<Integer> profilesIdList = new ArrayList<>();
        profilesIdList.add(2);
        userProfilesInsertionDTO = new UserProfilesInsertionDTO();
        userProfilesInsertionDTO.setProfileIds(profilesIdList);
        userProfilesInsertionDTO.setUserId("prova@test.com");
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
    void testCheckProfilesList() {
        List<Integer> intLIst = new ArrayList<>();
        intLIst.add(1);
        intLIst.add(2);

        Uni<List<Void>> result = userProfilesService.checkProfileList(intLIst);
        result.subscribe().with(Assertions::assertNotNull);
    }

    @Test
    void testInsertUserProfilesOK() {
        List<UserProfiles> userProfilesList = new ArrayList<>();
        userProfilesList.add(userProfiles);
        when(userProfilesMapper.toEntityInsertion(any(UserProfilesInsertionDTO.class))).thenReturn(userProfilesList);
        when(userProfilesService.isUserProfileExisting(userProfiles)).thenReturn(Uni.createFrom().item(true));
        when(userProfilesRepository.findById(userProfiles.getUserProfilesPK())).thenReturn(Uni.createFrom().nullItem());
        when(userProfilesRepository.persist(any(UserProfiles.class))).thenReturn(Uni.createFrom().item(userProfiles));
        when(profileRepository.findById(userProfiles.getUserProfilesPK().getProfileId())).thenReturn(Uni.createFrom().item(new Profile()));
        when(userRepository.findById(userProfiles.getUserProfilesPK().getUserId())).thenReturn(Uni.createFrom().item(new User()));

        userProfilesService.insertSingleUserProfile(userProfiles)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(userProfiles);
        Uni<List<UserProfiles>> result = userProfilesService.insertUserProfiles(userProfilesInsertionDTO);
        result.subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(userProfilesList);
    }

    @Test
    void testFindByIdOk() {
        when(userProfilesRepository.findById(any(UserProfilesPK.class))).thenReturn(Uni.createFrom().item(userProfiles));

        Uni<UserProfiles> result = userProfilesService.getById("prova@test.com", 2);

        result.subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(userProfiles);

        verify(userProfilesRepository).findById(any(UserProfilesPK.class));
    }

    @Test
    void testFindByIdNull() {
        UserProfilesPK userProfilesPK = new UserProfilesPK("prova@test.com", 2);
        when(userProfilesRepository.findById(userProfilesPK)).thenReturn(Uni.createFrom().nullItem());

        userProfilesService.getById(userProfilesPK.getUserId(), userProfilesPK.getProfileId())
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailed();
    }

    @Test
    void testDeleteUserProfilesOk() {
        when(userProfilesRepository.findById(any(UserProfilesPK.class))).thenReturn(Uni.createFrom().item(userProfiles));
        when(userProfilesRepository.delete(any(UserProfiles.class))).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> result = userProfilesService.deleteUserProfiles(userProfilesPK);

        result.subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(userProfilesRepository).findById(any(UserProfilesPK.class));
        verify(userProfilesRepository).delete(any(UserProfiles.class));
    }

    @Test
    void testDeleteUserProfilesNull() {
        UserProfilesPK userProfilesPK = new UserProfilesPK("prova@test.com", 2);
        when(userProfilesRepository.findById(userProfilesPK)).thenReturn(Uni.createFrom().nullItem());

        userProfilesService.deleteUserProfiles(userProfilesPK)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailed();
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
    public void testUpdateUserProfiles_Success() {
        UserProfilesInsertionDTO dto = new UserProfilesInsertionDTO();
        dto.setUserId("prova@test.com");
        dto.setProfileIds(List.of(1, 2, 3));

        List<UserProfiles> userProfilesToUpdate = new ArrayList<>();
        userProfilesToUpdate.add(userProfiles);

        when(userProfilesMapper.toEntityInsertion(dto)).thenReturn(userProfilesToUpdate);
        when(userRepository.findById(dto.getUserId())).thenReturn(Uni.createFrom().item(new User()));
        when(profileRepository.findById(anyInt())).thenReturn(Uni.createFrom().item(new Profile()));
        when(userProfilesRepository.findByUserId(dto.getUserId())).thenReturn(Uni.createFrom().item(new ArrayList<>()));
        when(userProfilesRepository.deleteUserProfiles(anyList())).thenReturn(Uni.createFrom().item(1L));
        when(userProfilesRepository.persist(userProfilesToUpdate)).thenReturn(Uni.createFrom().voidItem());

        userProfilesService.updateUserProfiles(dto)
                .subscribe().with(Assertions::assertNotNull);

        verify(userProfilesMapper).toEntityInsertion(dto);
        verify(userRepository).findById(dto.getUserId());
        verify(profileRepository, times(3)).findById(anyInt());
        verify(userProfilesRepository).deleteUserProfiles(anyList());
        verify(userProfilesRepository).persist(anyList());
    }

}
