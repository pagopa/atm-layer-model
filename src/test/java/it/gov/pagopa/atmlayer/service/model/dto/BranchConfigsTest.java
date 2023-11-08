package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class BranchConfigsTest {

  @Test
  void testCanEqual() {
    assertFalse((new BranchConfigs()).canEqual("Other"));
  }

  @Test
  void testConstructor() {
    BranchConfigs actualBranchConfigs = new BranchConfigs();
    UUID randomUUIDResult = UUID.randomUUID();
    actualBranchConfigs.setBranchDefaultTemplateId(randomUUIDResult);
    actualBranchConfigs.setBranchDefaultTemplateVersion(1L);
    actualBranchConfigs.setBranchId("namesurname/featurebranch");
    ArrayList<TerminalConfigs> terminalConfigsList = new ArrayList<>();
    actualBranchConfigs.setTerminals(terminalConfigsList);
    actualBranchConfigs.toString();
    assertSame(randomUUIDResult, actualBranchConfigs.getBranchDefaultTemplateId());
    assertEquals(1L, actualBranchConfigs.getBranchDefaultTemplateVersion().longValue());
    assertEquals("namesurname/featurebranch", actualBranchConfigs.getBranchId());
    assertSame(terminalConfigsList, actualBranchConfigs.getTerminals());
  }

  @Test
  void testEquals() {
    BranchConfigs branchConfigs = new BranchConfigs();
    branchConfigs.setBranchDefaultTemplateId(UUID.randomUUID());
    branchConfigs.setBranchDefaultTemplateVersion(1L);
    branchConfigs.setBranchId("namesurname/featurebranch");
    branchConfigs.setTerminals(new ArrayList<>());
    assertNotEquals(branchConfigs, null);
  }
}

