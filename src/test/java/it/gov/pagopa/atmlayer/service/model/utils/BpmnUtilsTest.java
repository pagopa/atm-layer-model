package it.gov.pagopa.atmlayer.service.model.utils;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class BpmnUtilsTest {

    @Test
    void testGetBpmnBankConfigPK() {
        String acquirerId = "ACQ123";
        BranchConfigs branchConfig = mock(BranchConfigs.class);

        when(branchConfig.getBranchDefaultTemplateId()).thenReturn(
                UUID.fromString("b0ac312f-6f9b-462b-a6f5-6e817533f36a"));
        when(branchConfig.getBranchDefaultTemplateVersion()).thenReturn(Long.valueOf("1"));
        when(branchConfig.getBranchId()).thenReturn("Branch123");

        Optional<BpmnBankConfigPK> result = BpmnUtils.getBpmnBankConfigPK(
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

    @Test
    void testGetBpmnBankConfigPKWhenTemplateIdIsNull() {
        String acquirerId = "ACQ123";
        BranchConfigs branchConfig = mock(BranchConfigs.class);

        when(branchConfig.getBranchDefaultTemplateId()).thenReturn(null);

        Optional<BpmnBankConfigPK> result = BpmnUtils.getBpmnBankConfigPK(acquirerId, branchConfig);

        assertFalse(result.isPresent(),
                "Result should be empty when either templateId or version is null");

        verify(branchConfig, times(1)).getBranchDefaultTemplateId();
        verifyNoMoreInteractions(branchConfig);
    }

    @Test
    void testGetBpmnBankConfigPKWhenVersionIsNull() {
        String acquirerId = "ACQ123";
        BranchConfigs branchConfig = mock(BranchConfigs.class);

        when(branchConfig.getBranchDefaultTemplateId()).thenReturn(UUID.randomUUID());
        when(branchConfig.getBranchDefaultTemplateVersion()).thenReturn(null);

        Optional<BpmnBankConfigPK> result = BpmnUtils.getBpmnBankConfigPK(acquirerId, branchConfig);

        assertFalse(result.isPresent(),
                "Result should be empty when either templateId or version is null");

        verify(branchConfig, times(1)).getBranchDefaultTemplateVersion();
    }
}

