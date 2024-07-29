package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BpmnUpgradeDtoTest {


  @Test
  void testCanEqual() {
    assertFalse((new BpmnUpgradeDto()).canEqual("Other"));
  }

  @Test
  void testConstructor() {
    BpmnUpgradeDto actualBpmnUpgradeDto = new BpmnUpgradeDto();
    actualBpmnUpgradeDto.setFile(
        Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile());
    actualBpmnUpgradeDto.setFilename("foo.txt");
    actualBpmnUpgradeDto.setFunctionType("MENU");
    UUID randomUUIDResult = UUID.randomUUID();
    actualBpmnUpgradeDto.setUuid(randomUUIDResult);
    actualBpmnUpgradeDto.toString();
    assertEquals("foo.txt", actualBpmnUpgradeDto.getFilename());
    assertEquals("MENU", actualBpmnUpgradeDto.getFunctionType());
    assertSame(randomUUIDResult, actualBpmnUpgradeDto.getUuid());
  }

  @Test
  void testEquals() {
    BpmnUpgradeDto bpmnUpgradeDto = new BpmnUpgradeDto();
    bpmnUpgradeDto.setFile(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile());
    bpmnUpgradeDto.setFilename("foo.txt");
    bpmnUpgradeDto.setFunctionType("MENU");
    bpmnUpgradeDto.setUuid(UUID.randomUUID());
    assertNotEquals(null, bpmnUpgradeDto);
  }
}

