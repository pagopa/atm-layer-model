package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class BpmnCreationDtoTest {

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
    actualBpmnCreationDto.setFunctionType(FunctionTypeEnum.MENU.name());
    String actualToStringResult = actualBpmnCreationDto.toString();
    assertEquals("foo.txt", actualBpmnCreationDto.getFilename());
    assertEquals(FunctionTypeEnum.MENU.name(), actualBpmnCreationDto.getFunctionType());
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
    bpmnCreationDto.setFunctionType(FunctionTypeEnum.MENU.name());
    assertNotEquals(bpmnCreationDto, null);
  }
}

