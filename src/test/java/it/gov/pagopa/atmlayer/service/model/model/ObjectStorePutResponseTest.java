package it.gov.pagopa.atmlayer.service.model.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ObjectStorePutResponseTest {

    @Test
    void testAllArgsConstructor() {
        ObjectStorePutResponse response = new ObjectStorePutResponse("12345");
        assertNotNull(response);
        assertEquals("12345", response.getStorageKey());
    }

    @Test
    void testNoArgsConstructor() {
        ObjectStorePutResponse response = new ObjectStorePutResponse();
        assertNotNull(response);
        assertNull(response.getStorageKey());
    }

    @Test
    void testBuilder() {
        ObjectStorePutResponse response = ObjectStorePutResponse.builder()
                .storageKey("67890")
                .build();

        assertNotNull(response);
        assertEquals("67890", response.getStorageKey());
    }

    @Test
    void testGetterAndSetter() {
        ObjectStorePutResponse response = new ObjectStorePutResponse();
        response.setStorageKey("54321");
        assertEquals("54321", response.getStorageKey());
    }

    @Test
    void testToString() {
        ObjectStorePutResponse response = new ObjectStorePutResponse("99999");
        String expectedToString = "ObjectStorePutResponse(storageKey=99999)";
        assertEquals(expectedToString, response.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        ObjectStorePutResponse response1 = new ObjectStorePutResponse("12345");
        ObjectStorePutResponse response2 = new ObjectStorePutResponse("12345");
        ObjectStorePutResponse response3 = new ObjectStorePutResponse("67890");
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}
