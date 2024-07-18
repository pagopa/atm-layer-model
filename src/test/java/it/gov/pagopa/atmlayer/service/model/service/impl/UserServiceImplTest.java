package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserProfilesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    UserProfilesService userProfilesService;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    private User user;
    private UserProfiles userProfiles;
    private List<UserProfiles> userProfilesList;
    private UserInsertionDTO userInsertionDTO;
    private UserInsertionWithProfilesDTO userInsertionWithProfilesDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        userProfiles = new UserProfiles();
        userProfiles.setUserProfilesPK(new UserProfilesPK("prova@test.com", 1));
        userProfiles.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userProfiles.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

        userProfilesList = new ArrayList<>();
        userProfilesList.add(userProfiles);

        user.setUserId("prova@test.com");
        user.setName("prova");
        user.setSurname("test");
        user.setUserProfiles(userProfilesList);

        userInsertionDTO = new UserInsertionDTO();
        userInsertionDTO.setUserId("prova@test.com");
        userInsertionDTO.setName("prova");
        userInsertionDTO.setSurname("test");

        userInsertionWithProfilesDTO = new UserInsertionWithProfilesDTO();
        userInsertionWithProfilesDTO.setUserId("prova@test.com");
        userInsertionWithProfilesDTO.setName("prova");
        userInsertionWithProfilesDTO.setSurname("test");
        userInsertionWithProfilesDTO.setProfileIds(List.of(1));
    }

    @Test
    void testInsertUserOK() {
        User user = new User();
        user.setUserId("prova@test.com");
        String userId = user.getUserId();
        UserInsertionDTO userInsertionDTO = new UserInsertionDTO();
        userInsertionDTO.setUserId("prova@test.com");
        userInsertionDTO.setName("prova");
        userInsertionDTO.setSurname("test");

        when(userMapper.toEntityInsertion(any(UserInsertionDTO.class))).thenReturn(user);
        when(userRepository.findById(user.getUserId())).thenReturn(Uni.createFrom().nullItem());
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userServiceImpl.insertUser(userInsertionDTO)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).persist(user);
    }

    @Test
    void findUserTest() {
        userServiceImpl.findUser(user.getUserId())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .getItem();

        verify(userRepository, times(1)).findByIdCustom(user.getUserId());
    }

    @Test
    void insertUserWithProfilesTestWithInitialUserNotFound() {
        when(userMapper.toEntityInsertionWithProfiles(any(UserInsertionWithProfilesDTO.class))).thenReturn(user);
        when(userRepository.findById(user.getUserId())).thenReturn(Uni.createFrom().nullItem());
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        User result = userServiceImpl.insertUserWithProfiles(userInsertionWithProfilesDTO)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .getItem();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user, result);

        verify(userMapper, times(1)).toEntityInsertionWithProfiles(any(UserInsertionWithProfilesDTO.class));
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).persist(any(User.class));
    }

    @Test
    void insertUserWithProfilesTestWithInitialUserFound() {
        when(userMapper.toEntityInsertionWithProfiles(any(UserInsertionWithProfilesDTO.class))).thenReturn(user);
        when(userRepository.findById(user.getUserId())).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userServiceImpl.insertUserWithProfiles(userInsertionWithProfilesDTO)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Un utente con lo stesso id esiste già");
    }

    @Test
    void testInsertUserExceptionCase() {

        UserInsertionDTO userInsertionDTO = new UserInsertionDTO();
        userInsertionDTO.setUserId("prova@test.com");
        userInsertionDTO.setName("prova");
        userInsertionDTO.setSurname("test");
        User user = new User();
        user.setUserId(userInsertionDTO.getUserId());
        user.setName(userInsertionDTO.getName());
        user.setSurname(userInsertionDTO.getSurname());

        when(userMapper.toEntityInsertion(any(UserInsertionDTO.class))).thenReturn(user);
        when(userRepository.findById("prova@test.com")).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userServiceImpl.insertUser(userInsertionDTO)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailed()
                .assertFailedWith(AtmLayerException.class, "Un utente con lo stesso id esiste già");

        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void testUpdateUser() {
        UserInsertionDTO dto = new UserInsertionDTO();
        dto.setUserId("Paolo@Rossi.com");
        dto.setName("Paolo");
        dto.setSurname("Rossi");

        User user = new User();
        user.setUserId(dto.getUserId());

        when(userServiceImpl.getById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userServiceImpl.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository).persist(user);
    }

    @Test
    void testUpdateUserSuccessPartialNameOnly() {
        UserInsertionDTO dto = new UserInsertionDTO();
        dto.setUserId("Paolo@Rossi.com");
        dto.setName("Paolo");
        dto.setSurname("");

        User user = new User();
        user.setUserId(dto.getUserId());

        when(userServiceImpl.getById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userServiceImpl.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository).persist(user);
    }

    @Test
    void testUpdateUserSuccessPartialSurnameOnly() {
        UserInsertionDTO dto = new UserInsertionDTO();
        dto.setUserId("Paolo@Rossi.com");
        dto.setName("");
        dto.setSurname("Rossi");

        User user = new User();
        user.setUserId(dto.getUserId());

        when(userServiceImpl.getById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userServiceImpl.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

    }

    @Test
    void testUpdateUserErrorAllFieldsBlank() {
        UserInsertionDTO dto = new UserInsertionDTO();
        dto.setUserId("");
        dto.setName("");
        dto.setSurname("");

        when(userServiceImpl.getById(any(String.class))).thenReturn(Uni.createFrom().item(new User()));

        userServiceImpl.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailed()
                .assertFailedWith(AtmLayerException.class, "Tutti i campi sono vuoti");

        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void testGetById() {
        String userId = "existentId";
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findByIdCustom(userId)).thenReturn(Uni.createFrom().item(user));

        userServiceImpl.getById(userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository).findByIdCustom(userId);
    }

    @Test
    void testFindByIdExceptionCase() {
        String userId = "nonExistentId";

        when(userRepository.findById(userId)).thenReturn(Uni.createFrom().nullItem());

        userServiceImpl.getById(userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailed()
                .assertFailedWith(AtmLayerException.class, "Nessun utente trovato per l'id selezionato");
    }

    @Test
    void testGetAllUsers() {
        List<User> userList = new ArrayList<>();
        User user = new User();
        userList.add(user);

        PanacheQuery<User> panacheQuery = mock(PanacheQuery.class);

        when(userRepository.findAllCustom()).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(Uni.createFrom().item(userList));

        Uni<List<User>> result = userServiceImpl.getAllUsers();

        result.subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(userList);
    }

    @Test
    void testDeleteOK() {
        String userId = "testUserId";
        User user = new User();

        when(userServiceImpl.getById(userId)).thenReturn(Uni.createFrom().item(user));
        when(userRepository.deleteById(userId)).thenReturn(Uni.createFrom().item(true));

        userServiceImpl.deleteUser(userId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(true);
    }

    @Test
    void testCheckFirstAccessWhenNoUsers() {
        long userCount = 0;

        User mockedUser = new User();
        mockedUser.setUserId("test@test.com");

        UserInsertionWithProfilesDTO userInsertionWithProfilesDTO = new UserInsertionWithProfilesDTO();
        userInsertionWithProfilesDTO.setUserId("test@test.com");
        List<Integer> profileIds = new ArrayList<>();
        profileIds.add(5);
        userInsertionWithProfilesDTO.setProfileIds(profileIds);

        when(userRepository.count()).thenReturn(Uni.createFrom().item(userCount));
        when(userMapper.toEntityInsertionWithProfiles(any(UserInsertionWithProfilesDTO.class))).thenReturn(mockedUser);
        when(userRepository.findById(anyString())).thenReturn(Uni.createFrom().nullItem());
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(mockedUser));

        userServiceImpl.checkFirstAccess(mockedUser.getUserId())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(null);

        verify(userRepository, times(1)).count();
        verify(userMapper, times(1)).toEntityInsertionWithProfiles(any(UserInsertionWithProfilesDTO.class));
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).persist(any(User.class));
    }


    @Test
    void testCheckFirstAccessWhenUsersExist() {
        String userId = "testUserId";
        long userCount = 5;

        when(userRepository.count()).thenReturn(Uni.createFrom().item(userCount));

        userServiceImpl.checkFirstAccess(userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);

        verify(userRepository, times(1)).count();
        verify(userProfilesService, never()).insertUserProfiles(any(UserProfilesInsertionDTO.class));
    }

}
