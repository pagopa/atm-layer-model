package it.gov.pagopa.atmlayer.service.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class BpmnUtilsTest {

  @Test
  public void testGetBpmnBankConfigPK() {

    BpmnAssociationDto bpmnAssociationDto = new BpmnAssociationDto();
    String acquirerId = "ACQ123";
    BranchConfigs branchConfig = mock(BranchConfigs.class);

    when(branchConfig.getBranchDefaultTemplateId()).thenReturn(
        UUID.fromString("b0ac312f-6f9b-462b-a6f5-6e817533f36a"));
    when(branchConfig.getBranchDefaultTemplateVersion()).thenReturn(Long.valueOf("1"));
    when(branchConfig.getBranchId()).thenReturn("Branch123");

    Optional<BpmnBankConfigPK> result = BpmnUtils.getBpmnBankConfigPK(bpmnAssociationDto,
        acquirerId, branchConfig);

    assertTrue(result.isPresent(), "Result should be present");
    BpmnBankConfigPK bpmnBankConfigPK = result.get();
    assertEquals(UUID.fromString("b0ac312f-6f9b-462b-a6f5-6e817533f36a"),
        bpmnBankConfigPK.getBpmnId());
    assertEquals(1L, bpmnBankConfigPK.getBpmnModelVersion());
    assertEquals("ACQ123", bpmnBankConfigPK.getAcquirerId());
    assertEquals("Branch123", bpmnBankConfigPK.getBranchId());
    assertEquals(BankConfigUtilityValues.NULL_VALUE.getValue(), bpmnBankConfigPK.getTerminalId());
  }
}

