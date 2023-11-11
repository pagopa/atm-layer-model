package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnBankConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class BpmnBankConfigServiceImplTest {

    @InjectMocks
    private BpmnBankConfigService bankConfigService;

    @Mock
    private BpmnBankConfigRepository bankConfigRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveList() {
        List<BpmnBankConfig> mockConfigs = Collections.singletonList(new BpmnBankConfig());

        when(bankConfigRepository.persist(mockConfigs))
                .thenReturn(Uni.createFrom().nullItem());

        Uni<Void> result = bankConfigService.saveList(mockConfigs);

        assertNotNull(result);
        assertNull(result.await().indefinitely());
    }

    @Test
    public void testFindByAcquirerIdAndFunctionType() {
        String acquirerId = "acquirer1";
        FunctionTypeEnum functionType = FunctionTypeEnum.MENU;

        List<BpmnBankConfig> mockResult = Collections.singletonList(new BpmnBankConfig());

        when(bankConfigRepository.findByAcquirerIdAndFunctionType(acquirerId, functionType.name()))
                .thenReturn(Uni.createFrom().item(mockResult));

        Uni<List<BpmnBankConfig>> result = bankConfigService.findByAcquirerIdAndFunctionType(acquirerId, functionType.name());

        assertNotNull(result);

        List<BpmnBankConfig> resultList = result.await().indefinitely();
        assertEquals(1, resultList.size());
    }

    @Test
    public void testDeleteByAcquirerIdAndFunctionType() {
        String acquirerId = "acquirer1";
        FunctionTypeEnum functionType = FunctionTypeEnum.MENU;

        when(bankConfigRepository.deleteByAcquirerIdAndFunctionType(acquirerId, functionType.name()))
                .thenReturn(Uni.createFrom().item(1L));

        Uni<Long> result = bankConfigService.deleteByAcquirerIdAndFunctionType(acquirerId, functionType.name());

        assertNotNull(result);

        Long deletedCount = result.await().indefinitely();
        assertEquals(1L, deletedCount);
    }

    @Test
    public void testFindByConfigurationsAndFunction() {
        String acquirerId = "acquirer1";
        String branchId = "branch1";
        String terminalId = "terminal1";
        FunctionTypeEnum functionType = FunctionTypeEnum.MENU;

        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setAcquirerId(acquirerId);
        bpmnBankConfigPK.setBranchId(branchId);
        bpmnBankConfigPK.setTerminalId(terminalId);
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        bpmnBankConfig.setFunctionType(functionType.name());

        List<BpmnBankConfig> mockResult = Collections.singletonList(bpmnBankConfig);

        when(bankConfigRepository.findByConfigAndFunctionType(acquirerId, branchId, terminalId, functionType.name()))
                .thenReturn(Uni.createFrom().item(mockResult));

        Uni<Optional<BpmnBankConfig>> result = bankConfigService.findByConfigurationsAndFunction(acquirerId, branchId, terminalId, functionType.name());

        assertNotNull(result);

        Optional<BpmnBankConfig> optionalResult = result.await().indefinitely();
        assertTrue(optionalResult.isPresent());

        BpmnBankConfig retrievedConfig = optionalResult.get();
        assertEquals(acquirerId, retrievedConfig.getBpmnBankConfigPK().getAcquirerId());
        assertEquals(branchId, retrievedConfig.getBpmnBankConfigPK().getBranchId());
        assertEquals(terminalId, retrievedConfig.getBpmnBankConfigPK().getTerminalId());
        assertEquals(functionType.name(), retrievedConfig.getFunctionType());
    }

    @Test
    public void testFindByConfigurationsAndFunctionWithEmptyList() {
        String acquirerId = "acquirer1";
        String branchId = "branch1";
        String terminalId = "terminal1";
        FunctionTypeEnum functionType = FunctionTypeEnum.MENU;

        when(bankConfigRepository.findByConfigAndFunctionType(acquirerId, branchId, terminalId, functionType.name()))
                .thenReturn(Uni.createFrom().item(Collections.emptyList()));

        Uni<Optional<BpmnBankConfig>> result = bankConfigService.findByConfigurationsAndFunction(acquirerId, branchId, terminalId, functionType.name());

        assertNotNull(result);

        Optional<BpmnBankConfig> optionalResult = result.await().indefinitely();
        assertFalse(optionalResult.isPresent());
    }

    @Test
    public void testFindByConfigurationsAndFunctionWithMultipleResults() {
        String acquirerId = "acquirer1";
        String branchId = "branch1";
        String terminalId = "terminal1";
        FunctionTypeEnum functionType = FunctionTypeEnum.MENU;

        BpmnBankConfig config1 = new BpmnBankConfig();
        BpmnBankConfig config2 = new BpmnBankConfig();
        List<BpmnBankConfig> mockResult = List.of(config1, config2);

        when(bankConfigRepository.findByConfigAndFunctionType(acquirerId, branchId, terminalId, functionType.name()))
                .thenReturn(Uni.createFrom().item(mockResult));

        // Modifica il test per catturare l'eccezione AtmLayerException
        assertThrows(AtmLayerException.class, () -> {
            bankConfigService.findByConfigurationsAndFunction(acquirerId, branchId, terminalId, functionType.name())
                    .await().indefinitely();
        }, "Multiple BPMN found for a single configuration.");
    }
}
