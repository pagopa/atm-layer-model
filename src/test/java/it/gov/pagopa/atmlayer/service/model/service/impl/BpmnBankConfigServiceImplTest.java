package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapperImpl;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnBankConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BpmnBankConfigServiceImplTest {
    @InjectMocks
    private BpmnBankConfigService bankConfigService;
    @Mock
    private BpmnBankConfigRepository bankConfigRepository;
    @Mock
    private BpmnConfigMapperImpl bpmnConfigMapper;

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
        String functionType = "MENU";
        List<BpmnBankConfig> mockResult = Collections.singletonList(new BpmnBankConfig());
        when(bankConfigRepository.findByAcquirerIdAndFunctionType(acquirerId, functionType))
                .thenReturn(Uni.createFrom().item(mockResult));
        Uni<List<BpmnBankConfig>> result = bankConfigService.findByAcquirerIdAndFunctionType(acquirerId, functionType);
        assertNotNull(result);
        List<BpmnBankConfig> resultList = result.await().indefinitely();
        assertEquals(1, resultList.size());
    }

    @Test
    public void testDeleteByAcquirerIdAndFunctionType() {
        String acquirerId = "acquirer1";
        String functionType = "MENU";
        when(bankConfigRepository.deleteByAcquirerIdAndFunctionType(acquirerId, functionType))
                .thenReturn(Uni.createFrom().item(1L));
        Uni<Long> result = bankConfigService.deleteByAcquirerIdAndFunctionType(acquirerId, functionType);
        assertNotNull(result);
        Long deletedCount = result.await().indefinitely();
        assertEquals(1L, deletedCount);
    }

    @Test
    public void testFindByConfigurationsAndFunctionOK() {
        List<BpmnBankConfig> expectedList = new ArrayList<>();
        expectedList.add(new BpmnBankConfig());
        when(bankConfigRepository.findByConfigAndFunctionType(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(expectedList));
        bankConfigService.findByConfigurationsAndFunction("acq", "branch", "terminal", "function")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(Optional.of(expectedList.get(0)));
    }

    @Test
    public void testFindByConfigurationsAndFunctionMultipleFound() {
        List<BpmnBankConfig> expectedList = new ArrayList<>();
        expectedList.add(new BpmnBankConfig());
        expectedList.add(new BpmnBankConfig());
        when(bankConfigRepository.findByConfigAndFunctionType(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(expectedList));
        bankConfigService.findByConfigurationsAndFunction("acq", "branch", "terminal", "function")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Multiple BPMN found for a single configuration.");
    }

    @Test
    public void testFindByConfigurationsAndFunctionEmptyList() {
        List<BpmnBankConfig> expectedList = new ArrayList<>();
        when(bankConfigRepository.findByConfigAndFunctionType(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(expectedList));
        bankConfigService.findByConfigurationsAndFunction("acq", "branch", "terminal", "function")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(Optional.empty());
    }

    @Test
    public void testfindByAcquirerIdOK(){
        List<BpmnBankConfig> expectedList = new ArrayList<>();
        expectedList.add(new BpmnBankConfig());
        when(bankConfigRepository.findByAcquirerId(any(String.class))).thenReturn(Uni.createFrom().item(expectedList));
        when(bpmnConfigMapper.toDTO(any(BpmnBankConfig.class))).thenReturn(new BpmnBankConfigDTO());
        bankConfigService.findByAcquirerId("acquirer")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }

    @Test
    public void testfindByAcquirerIdEmptyList(){
        List<BpmnBankConfig> expectedList = new ArrayList<>();
        when(bankConfigRepository.findByAcquirerId(any(String.class))).thenReturn(Uni.createFrom().item(expectedList));
        bankConfigService.findByAcquirerId("acquirer")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"No BPMN configurations found for this bank");
    }


}
