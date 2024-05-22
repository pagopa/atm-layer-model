package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class UserProfileCreationDtoTest {

    @Test
    void testConstructor(){
        UserProfileCreationDto dto = new UserProfileCreationDto();
        dto.setUserId("email@domain.com");
        dto.setProfile(3);
        assertEquals("email@domain.com", dto.getUserId());
        assertEquals(UserProfileEnum.ADMIN.getValue(), dto.getProfile());
    }

    @Test
    void testEquals() {
        UserProfileCreationDto dto1 = new UserProfileCreationDto();
        UserProfileCreationDto dto2 = new UserProfileCreationDto();
        assertEquals(dto1, dto2);
        int expectedHashCodeResult = dto1.hashCode();
        assertEquals(expectedHashCodeResult, dto2.hashCode());
    }
}