package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BranchConfigsTest {

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
    assertNotEquals(null, branchConfigs);
  }
}

