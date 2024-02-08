package it.gov.pagopa.atmlayer.service.model.entity;

import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void testSetGetUserId() {
        UserProfile user = new UserProfile();
        user.setUserId("user@domain.com");
        assertEquals("user@domain.com", user.getUserId());
        assertEquals("user@domain.com", user.getUserId());
    }

    @Test
    void testSetGetProfile() {
        UserProfile user = new UserProfile();
        user.setProfile(1);
        assertEquals(UserProfileEnum.GUEST.getValue(), user.getProfile());
    }

    @Test
    void testSetGetCreatedAt(){
        UserProfile user = new UserProfile();
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        assertEquals(new Timestamp(System.currentTimeMillis()), user.getCreatedAt());
    }

    @Test
    void testSetGetLastUpdatedAt(){
        UserProfile user = new UserProfile();
        user.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
        assertEquals(new Timestamp(System.currentTimeMillis()), user.getLastUpdatedAt());
    }
}