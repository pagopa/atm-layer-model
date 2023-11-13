package it.gov.pagopa.atmlayer.service.model.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ObjectStorePutResponseTest {

  @Test
  public void testAllArgsConstructor() {
    ObjectStorePutResponse response = new ObjectStorePutResponse("12345");
    assertNotNull(response);
    assertEquals("12345", response.getStorage_key());
  }

  @Test
  public void testNoArgsConstructor() {
    ObjectStorePutResponse response = new ObjectStorePutResponse();
    assertNotNull(response);
    assertNull(response.getStorage_key());
  }

  @Test
  public void testBuilder() {
    ObjectStorePutResponse response = ObjectStorePutResponse.builder()
        .storage_key("67890")
        .build();

    assertNotNull(response);
    assertEquals("67890", response.getStorage_key());
  }

  @Test
  public void testGetterAndSetter() {
    ObjectStorePutResponse response = new ObjectStorePutResponse();
    response.setStorage_key("54321");
    assertEquals("54321", response.getStorage_key());
  }

  @Test
  public void testToString() {
    ObjectStorePutResponse response = new ObjectStorePutResponse("99999");
    String expectedToString = "ObjectStorePutResponse(storage_key=99999)";
    assertEquals(expectedToString, response.toString());
  }

  @Test
  public void testEqualsAndHashCode() {
    ObjectStorePutResponse response1 = new ObjectStorePutResponse("12345");
    ObjectStorePutResponse response2 = new ObjectStorePutResponse("12345");
    ObjectStorePutResponse response3 = new ObjectStorePutResponse("67890");
    assertEquals(response1, response2);
    assertNotEquals(response1, response3);
    assertEquals(response1.hashCode(), response2.hashCode());
    assertNotEquals(response1.hashCode(), response3.hashCode());
  }
}
