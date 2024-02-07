package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserProfileDtoTest {

    @Test
    void testConstructor() {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId("user@domain.com");
        dto.setProfile(UserProfileEnum.GUEST);
        dto.setVisible(true);
        dto.setAdmin(false);
        dto.setEditable(false);
        dto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        dto.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
        assertEquals("user@domain.com", dto.getUserId());
        assertEquals(UserProfileEnum.GUEST, dto.getProfile());
        assertEquals(true, dto.getVisible());
        assertEquals(false, dto.getAdmin());
        assertEquals(false, dto.getEditable());
    }

    @Test
    void testEquals(){
        UserProfileDto dto1 = new UserProfileDto();
        UserProfileDto dto2 = new UserProfileDto();
        assertEquals(dto1, dto2);
        int expectedHashCodeResult = dto1.hashCode();
        assertEquals(expectedHashCodeResult, dto2.hashCode());
    }
}