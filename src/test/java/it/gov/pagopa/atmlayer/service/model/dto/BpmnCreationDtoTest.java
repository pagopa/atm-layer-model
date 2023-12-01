package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Paths;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class BpmnCreationDtoTest {

  @Test
  void testCanEqual() {
    assertFalse((new BpmnCreationDto()).canEqual("Other"));
  }

  @Test
  void testConstructor() {
    BpmnCreationDto actualBpmnCreationDto = new BpmnCreationDto();
    actualBpmnCreationDto.setFile(
        Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile());
    actualBpmnCreationDto.setFilename("foo.txt");
    actualBpmnCreationDto.setFunctionType("MENU");
    String actualToStringResult = actualBpmnCreationDto.toString();
    assertEquals("foo.txt", actualBpmnCreationDto.getFilename());
    assertEquals("MENU", actualBpmnCreationDto.getFunctionType());
    assertEquals(
        String.join("", "BpmnCreationDto(file=",
            Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toString(),
            ", filename=foo.txt, functionType=MENU)"),
        actualToStringResult);
  }

  @Test
  void testEquals() {
    BpmnCreationDto bpmnCreationDto = new BpmnCreationDto();
    bpmnCreationDto.setFile(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile());
    bpmnCreationDto.setFilename("foo.txt");
    bpmnCreationDto.setFunctionType("MENU");
    assertNotEquals(null, bpmnCreationDto);
  }
}

