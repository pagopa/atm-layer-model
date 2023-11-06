package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class BranchDtoTest {

  @Test
  void testCanEqual() {
    assertFalse((new BranchDto()).canEqual("Other"));
  }

  @Test
  void testConstructor() {
    BranchDto actualBranchDto = new BranchDto();
    actualBranchDto.setBranchId("namesurname/featurebranch");
    ArrayList<String> stringList = new ArrayList<>();
    actualBranchDto.setTerminalId(stringList);
    String actualToStringResult = actualBranchDto.toString();
    assertEquals("namesurname/featurebranch", actualBranchDto.getBranchId());
    assertSame(stringList, actualBranchDto.getTerminalId());
    assertEquals("BranchDto(branchId=namesurname/featurebranch, terminalId=[])", actualToStringResult);
  }

  @Test
  void testEquals() {
    BranchDto branchDto = new BranchDto();
    branchDto.setBranchId("namesurname/featurebranch");
    branchDto.setTerminalId(new ArrayList<>());
    assertNotEquals(branchDto, null);
  }
}

