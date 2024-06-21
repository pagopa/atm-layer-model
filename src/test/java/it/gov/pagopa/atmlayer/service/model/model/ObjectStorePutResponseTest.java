package it.gov.pagopa.atmlayer.service.model.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ObjectStorePutResponseTest {

    @Test
    void testAllArgsConstructor() {
        ObjectStoreResponse response = new ObjectStoreResponse("12345");
        assertNotNull(response);
        assertEquals("12345", response.getStorageKey());
    }

    @Test
    void testNoArgsConstructor() {
        ObjectStoreResponse response = new ObjectStoreResponse();
        assertNotNull(response);
        assertNull(response.getStorageKey());
    }

    @Test
    void testBuilder() {
        ObjectStoreResponse response = ObjectStoreResponse.builder()
                .storageKey("67890")
                .build();

        assertNotNull(response);
        assertEquals("67890", response.getStorageKey());
    }

    @Test
    void testGetterAndSetter() {
        ObjectStoreResponse response = new ObjectStoreResponse();
        response.setStorageKey("54321");
        assertEquals("54321", response.getStorageKey());
    }

    @Test
    void testToString() {
        ObjectStoreResponse response = new ObjectStoreResponse("99999");
        String expectedToString = "ObjectStoreResponse(storageKey=99999)";
        assertEquals(expectedToString, response.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        ObjectStoreResponse response1 = new ObjectStoreResponse("12345");
        ObjectStoreResponse response2 = new ObjectStoreResponse("12345");
        ObjectStoreResponse response3 = new ObjectStoreResponse("67890");
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}
