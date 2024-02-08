package it.gov.pagopa.atmlayer.service.model.enumeration;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserProfileEnumTest {

    @Test
    void testGetValue() {
        assertEquals(1, UserProfileEnum.GUEST.getValue());
        assertEquals(2, UserProfileEnum.OPERATOR.getValue());
        assertEquals(3, UserProfileEnum.ADMIN.getValue());
    }

    @Test
    void testValueOf() {
        assertEquals(UserProfileEnum.valueOf(1), UserProfileEnum.GUEST);
        assertEquals(UserProfileEnum.valueOf(2), UserProfileEnum.OPERATOR);
        assertEquals(UserProfileEnum.valueOf(3), UserProfileEnum.ADMIN);
    }
}