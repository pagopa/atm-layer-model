package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

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
    actualBpmnCreationDto.setDescription("description");
    String actualToStringResult = actualBpmnCreationDto.toString();
    assertEquals("foo.txt", actualBpmnCreationDto.getFilename());
    assertEquals("MENU", actualBpmnCreationDto.getFunctionType());
    assertEquals("description",actualBpmnCreationDto.getDescription());
    assertEquals(
        String.join("", "BpmnCreationDto(file=",
            Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toString(),
            ", filename=foo.txt, functionType=MENU, description=description)"),
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

