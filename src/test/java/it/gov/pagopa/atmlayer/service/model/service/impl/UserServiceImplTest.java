package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
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

        userService.insertUser(userInsertionDTO)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).persist(user);
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

        userService.insertUser(userInsertionDTO)
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

        when(userRepository.findById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userService.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
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

        when(userRepository.findById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userService.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
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

        when(userRepository.findById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userService.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository).persist(user);
    }

    @Test
    void testUpdateUserErrorAllFieldsBlank() {
        UserInsertionDTO dto = new UserInsertionDTO();
        dto.setUserId("Paolo@Rossi.com");
        dto.setName("");
        dto.setSurname("");

        when(userRepository.findById(any(String.class))).thenReturn(Uni.createFrom().item(new User()));

        userService.updateUser(dto).subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailed()
                .assertFailedWith(AtmLayerException.class, "Tutti i campi sono vuoti");

        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void testFindById() {
        String userId = "existentId";
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Uni.createFrom().item(user));

        userService.findById(userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository).findById(userId);
    }

    @Test
    void testFindByIdExceptionCase() {
        String userId = "nonExistentId";

        when(userRepository.findById(userId)).thenReturn(Uni.createFrom().nullItem());

        userService.findById(userId)
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

        when(userRepository.findAll()).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(Uni.createFrom().item(userList));

        Uni<List<User>> result = userService.getAllUsers();

        result.subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(userList);
    }

    @Test
    void testDeleteOK() {
        String userId = "testUserId";
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Uni.createFrom().item(user));
        when(userRepository.deleteById(userId)).thenReturn(Uni.createFrom().item(true));

        userService.deleteUser(userId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(true);
    }

}
