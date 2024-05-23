package it.gov.pagopa.atmlayer.service.model.service.impl;

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

    @Test
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
    }

    @Test
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
