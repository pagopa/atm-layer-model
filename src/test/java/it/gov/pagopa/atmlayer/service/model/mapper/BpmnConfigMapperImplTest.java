package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BpmnConfigMapperImplTest {

    public BpmnConfigMapperImpl bpmnConfigMapperImpl;

    @BeforeEach
    public void setUp() {
        bpmnConfigMapperImpl = new BpmnConfigMapperImpl();
    }

    @Test
    public void toDTOtest_null(){
        BpmnBankConfig resourceFile = null;
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(resourceFile);
        assertNull(resource);
    }

    @Test
    public void toDTOtest(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(UUID.randomUUID());
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);

        assertNotNull(resource);
        assertEquals(bpmnBankConfig.getBpmnBankConfigPK().getBpmnId(), resource.getBpmnId());
        assertEquals(bpmnBankConfig.getFunctionType(), resource.getFunctionType());
        assertEquals(bpmnBankConfig.getBpmnBankConfigPK().getBpmnModelVersion(), resource.getBpmnModelVersion());
        assertEquals(bpmnBankConfig.getBpmnBankConfigPK().getBranchId(), resource.getBranchId());
        assertEquals(bpmnBankConfig.getBpmnBankConfigPK().getAcquirerId(), resource.getAcquirerId());
        assertEquals(bpmnBankConfig.getBpmnBankConfigPK().getTerminalId(), resource.getTerminalId());
        assertEquals(bpmnBankConfig.getCreatedAt(), resource.getCreatedAt());
        assertEquals(bpmnBankConfig.getLastUpdatedAt(), resource.getLastUpdatedAt());
        assertEquals(bpmnBankConfig.getCreatedBy(), resource.getCreatedBy());
        assertEquals(bpmnBankConfig.getLastUpdatedBy(), resource.getLastUpdatedBy());

    }

    @Test
    public void bpmnBankConfigDTOToBpmnBankConfigPKtest_null(){
        BpmnBankConfigDTO bankConfigDTO = null;
        BpmnBankConfigPK resource = bpmnConfigMapperImpl.bpmnBankConfigDTOToBpmnBankConfigPK(bankConfigDTO);
        assertNull(resource);
    }

    @Test
    public void bpmnBankConfigDTOToBpmnBankConfigPKtest(){
        BpmnBankConfigDTO bankConfigDTO = new BpmnBankConfigDTO();
        BpmnBankConfigPK resource = bpmnConfigMapperImpl.bpmnBankConfigDTOToBpmnBankConfigPK(bankConfigDTO);

        assertNotNull(resource);
        assertEquals(bankConfigDTO.getBpmnId(), resource.getBpmnId());
        assertEquals(bankConfigDTO.getTerminalId(), resource.getTerminalId());
        assertEquals(bankConfigDTO.getBpmnModelVersion(), resource.getBpmnModelVersion());
        assertEquals(bankConfigDTO.getBranchId(), resource.getBranchId());
        assertEquals(bankConfigDTO.getAcquirerId(), resource.getAcquirerId());
    }


    @Test
    public void toEntity_null(){
        BpmnBankConfigDTO bankConfigDTO = null;
        BpmnBankConfig resource = bpmnConfigMapperImpl.toEntity(bankConfigDTO);
        assertNull(resource);
    }

    @Test
    public void toEntity(){
        BpmnBankConfigDTO bankConfigDTO = new BpmnBankConfigDTO();
        BpmnBankConfig resource = bpmnConfigMapperImpl.toEntity(bankConfigDTO);

        assertNotNull(resource);
        assertEquals(bankConfigDTO.getFunctionType(), resource.getFunctionType());
        assertEquals(bankConfigDTO.getCreatedAt(), resource.getCreatedAt());
        assertEquals(bankConfigDTO.getLastUpdatedAt(), resource.getLastUpdatedAt());
        assertEquals(bankConfigDTO.getCreatedBy(), resource.getCreatedBy());
        assertEquals(bankConfigDTO.getLastUpdatedBy(), resource.getLastUpdatedBy());

    }

    @Test
    public void bpmnBankConfigBpmnBankConfigPKBpmnIdTest_null_BpmnBankConfigPK(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        bpmnBankConfig.setBpmnBankConfigPK(null);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);
        assertNull(resource.getBpmnId());
    }

    @Test
    public void bpmnBankConfigBpmnBankConfigPKBpmnIdTest_null_BpmnId(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(null);
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);

        assertNull(resource.getBpmnId());
    }

    @Test
    public void bpmnBankConfigBpmnBankConfigPKBpmnIdTest(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(UUID.randomUUID());
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);

        assertNotNull(resource.getBpmnId());
        assertEquals(bpmnBankConfigPK.getBpmnId(), resource.getBpmnId());

    }


    @Test
    public void bpmnBankConfigBpmnBankConfigPKBpmnModelVersionTest(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnModelVersion(1L);
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);

        assertNotNull(resource.getBpmnModelVersion());
        assertEquals(bpmnBankConfigPK.getBpmnModelVersion(), resource.getBpmnModelVersion());

    }

    @Test
    public void bpmnBankConfigBpmnBankConfigPKAcquirerIdTest(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setAcquirerId("id");
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);

        assertNotNull(resource.getAcquirerId());
        assertEquals(bpmnBankConfigPK.getAcquirerId(), resource.getAcquirerId());

    }

    @Test
    public void bpmnBankConfigBpmnBankConfigPKBranchIdTest_null_BranchId(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBranchId(null);
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);

        assertNull(resource.getBranchId());
    }

    @Test
    public void bpmnBankConfigBpmnBankConfigPKTerminalId_null_TerminalId(){
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setTerminalId(null);
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        BpmnBankConfigDTO resource = bpmnConfigMapperImpl.toDTO(bpmnBankConfig);

        assertNull(resource.getTerminalId());
    }

}
