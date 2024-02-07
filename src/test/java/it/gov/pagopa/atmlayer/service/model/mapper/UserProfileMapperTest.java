package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileDto;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;
import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserProfileMapperTest {
    private UserProfileMapper userProfileMapper;

    @BeforeEach
    public void setUp() {
        userProfileMapper = new UserProfileMapperImpl();
    }

    @Test
    void testToUserProfileDto() {
        UserProfile userProfile = mock(UserProfile.class);
        Timestamp create = new Timestamp(System.currentTimeMillis());
        Timestamp update = new Timestamp(System.currentTimeMillis());

        when(userProfile.getUserId()).thenReturn("email@domain.com");
        when(userProfile.getProfile()).thenReturn(UserProfileEnum.GUEST.getValue());
        when(userProfile.getCreatedAt()).thenReturn(create);
        when(userProfile.getLastUpdatedAt()).thenReturn(update);
        UserProfileDto userProfileDto = userProfileMapper.toUserProfileDto(userProfile);

        assertNotNull(userProfileDto);
        assertEquals("email@domain.com", userProfileDto.getUserId());
        assertEquals(UserProfileEnum.GUEST, userProfileDto.getProfile());
        assertEquals(create, userProfileDto.getCreatedAt());
        assertEquals(update, userProfileDto.getLastUpdatedAt());
    }

    @Test
    void testToUserProfile() {
        UserProfileCreationDto userProfileDto = mock(UserProfileCreationDto.class);

        Timestamp create = new Timestamp(System.currentTimeMillis());
        Timestamp update = new Timestamp(System.currentTimeMillis());

        when(userProfileDto.getUserId()).thenReturn("email@domain.com");
        when(userProfileDto.getProfile()).thenReturn(UserProfileEnum.GUEST.getValue());
        when(userProfileDto.getCreatedAt()).thenReturn(create);
        when(userProfileDto.getLastUpdatedAt()).thenReturn(update);
        UserProfile userProfile = userProfileMapper.toUserProfile(userProfileDto);

        assertNotNull(userProfile);
        assertEquals("email@domain.com", userProfile.getUserId());
        assertEquals(UserProfileEnum.GUEST.getValue(), userProfile.getProfile());
        assertEquals(create, userProfile.getCreatedAt());
        assertEquals(update, userProfile.getLastUpdatedAt());
    }

    @Test
    void testGetEnumValue() {
        assertEquals(UserProfileEnum.valueOf(1), userProfileMapper.getEnumValue(1));
        assertEquals(UserProfileEnum.valueOf(2), userProfileMapper.getEnumValue(2));
        assertEquals(UserProfileEnum.valueOf(3), userProfileMapper.getEnumValue(3));
    }

    @Test
    void testToUserProfileDtoWithProfileMapping() {
        UserProfile userProfile = new UserProfile();
        UserProfileDto userProfileDto;

        userProfile.setProfile(UserProfileEnum.ADMIN.getValue());
        userProfileDto = userProfileMapper.toUserProfileDtoWithProfileMapping(userProfile);
        assertEquals(true, userProfileDto.getAdmin());
        assertEquals(true, userProfileDto.getEditable());
        assertEquals(true, userProfileDto.getVisible());

        userProfile.setProfile(UserProfileEnum.OPERATOR.getValue());
        userProfileDto = userProfileMapper.toUserProfileDtoWithProfileMapping(userProfile);
        assertEquals(false, userProfileDto.getAdmin());
        assertEquals(true, userProfileDto.getEditable());
        assertEquals(true, userProfileDto.getVisible());

        userProfile.setProfile(UserProfileEnum.GUEST.getValue());
        userProfileDto = userProfileMapper.toUserProfileDtoWithProfileMapping(userProfile);
        assertEquals(false, userProfileDto.getAdmin());
        assertEquals(false, userProfileDto.getEditable());
        assertEquals(true, userProfileDto.getVisible());
    }

    @Test
    void testToDtoList() {
        UserProfile userProfile = mock(UserProfile.class);
        List<UserProfile> list = new ArrayList<>();
        list.add(userProfile);
        List<UserProfileDto> listDto = userProfileMapper.toDtoList(list);
        assertNotNull(listDto);
        assertEquals(list.size(), listDto.size());

    }
}