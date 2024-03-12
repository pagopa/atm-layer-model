package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfileMapper;
import it.gov.pagopa.atmlayer.service.model.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class UserProfileServiceImplTest {

    @InjectMocks
    UserProfileServiceImpl userProfileService;
    @Mock
    UserProfileRepository userProfileRepository;

    @InjectMock
    UserProfileMapper userProfileMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserAlreadyExist() {
        String userId = "email@domain.com";

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        UserProfileCreationDto userProfileDto = new UserProfileCreationDto();
        userProfileDto.setUserId(userId);

        when(userProfileMapper.toUserProfile(any(UserProfileCreationDto.class))).thenReturn(userProfile);
        when(userProfileRepository.findUserId(any(String.class))).thenReturn(Uni.createFrom().item(userProfile));
        when(userProfileService.findByUserId(any(String.class))).thenReturn(Uni.createFrom().item(userProfile));
        userProfileService.createUser(userProfileDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Esiste già un utente con lo stesso Id");
    }

    @Test
    void testDeleteUserNotExist() {
        String userId = "email@domain.com";
        UserProfile userProfile = new UserProfile();

        when(userProfileRepository.findUserId(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        userProfileService.deleteUser(userId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Un utente con l'Id email@domain.com non esiste");
    }
}