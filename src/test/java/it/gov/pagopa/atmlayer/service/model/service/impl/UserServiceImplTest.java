package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
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

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

   /* @Test
    void testInsertUserOK() {
        String userId = "testUserId";
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Uni.createFrom().nullItem());
        when(userRepository.persist(any(User.class))).thenReturn(Uni.createFrom().item(user));

        userService.insertUser(user)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(user);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).persist(user);
    }*/

    /*@Test
    void testInsertUserExceptionCase() {
        String userId = "testUserId";
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(any(String.class))).thenReturn(Uni.createFrom().item(new User()));

        userService.insertUser(user)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailed()
                .assertFailedWith(AtmLayerException.class, "Un utente con lo stesso id esiste già");

        verify(userRepository, never()).persist(any(User.class));
    }*/

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
