package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BranchDtoTest {

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
    assertNotEquals(null, branchDto);
  }
}

