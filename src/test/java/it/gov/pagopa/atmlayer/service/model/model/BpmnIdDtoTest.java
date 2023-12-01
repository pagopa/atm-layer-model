package it.gov.pagopa.atmlayer.service.model.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
class BpmnIdDtoTest {

  @Test
  void testEqualsAndHashCode() {
    BpmnIdDto dto1 = new BpmnIdDto(UUID.randomUUID(), 1L);
    BpmnIdDto dto2 = new BpmnIdDto(UUID.randomUUID(), 1L);

    assertNotEquals(dto1, dto2);
    assertNotEquals(null, dto1);

    BpmnIdDto dto3 = new BpmnIdDto(UUID.randomUUID(), 2L);
    assertNotEquals(dto1, dto3);
  }

  @Test
  void testToString() {
    UUID uuid = UUID.randomUUID();
    BpmnIdDto dto = new BpmnIdDto(uuid, 1L);

    String expectedToString = "BpmnIdDto(bpmnId=" + uuid + ", modelVersion=1)";
    assertEquals(expectedToString, dto.toString());
  }

  @Test
  void testGetterAndSetter() {
    BpmnIdDto dto = new BpmnIdDto(UUID.randomUUID(), 1L);

    UUID newUuid = UUID.randomUUID();
    dto.setBpmnId(newUuid);
    assertEquals(newUuid, dto.getBpmnId());

    Long newModelVersion = 2L;
    dto.setModelVersion(newModelVersion);
    assertEquals(newModelVersion, dto.getModelVersion());
  }
}

