package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class BpmnAssociationDtoTest {

  @Test
  void testCanEqual() {
    assertFalse((new BpmnAssociationDto()).canEqual("Other"));
  }

  @Test
  void testConstructor() {
    UUID defaultTemplateId = UUID.randomUUID();
    ArrayList<BranchConfigs> branchConfigsList = new ArrayList<>();
    BpmnAssociationDto actualBpmnAssociationDto = new BpmnAssociationDto(defaultTemplateId, 1L,
        branchConfigsList);
    ArrayList<BranchConfigs> branchConfigsList1 = new ArrayList<>();
    actualBpmnAssociationDto.setBranchesConfigs(branchConfigsList1);
    UUID randomUUIDResult = UUID.randomUUID();
    actualBpmnAssociationDto.setDefaultTemplateId(randomUUIDResult);
    actualBpmnAssociationDto.setDefaultTemplateVersion(1L);
    actualBpmnAssociationDto.toString();
    List<BranchConfigs> branchesConfigs = actualBpmnAssociationDto.getBranchesConfigs();
    assertSame(branchConfigsList1, branchesConfigs);
    assertEquals(branchConfigsList, branchesConfigs);
    assertSame(randomUUIDResult, actualBpmnAssociationDto.getDefaultTemplateId());
    assertEquals(1L, actualBpmnAssociationDto.getDefaultTemplateVersion().longValue());
  }

  @Test
  void testEquals() {
    BpmnAssociationDto bpmnAssociationDto = new BpmnAssociationDto();
    BpmnAssociationDto bpmnAssociationDto1 = new BpmnAssociationDto();
    assertEquals(bpmnAssociationDto, bpmnAssociationDto1);
    int expectedHashCodeResult = bpmnAssociationDto.hashCode();
    assertEquals(expectedHashCodeResult, bpmnAssociationDto1.hashCode());
  }
}

