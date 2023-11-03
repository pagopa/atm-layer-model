package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.dto.TerminalConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class BpmnUtilsTest {
    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = new File("C:\\DEV\\PagoPA\\ATM-LAYER\\GitHub\\model\\src\\test\\resources\\Test.bpmn");
    }

    @Test
    void testFileToByteArray() throws IOException {
        byte[] byteArray = BpmnUtils.fileToByteArray(testFile);
        assertEquals(testFile.length(), byteArray.length);
    }

    @Test
    void testCalculateSha256() throws NoSuchAlgorithmException, IOException {
        String expectedSha256 = "4e42373c2dbf12a9ec56839ef0b6a91ce54e624cdc342ee44b08483017193c7e";
        String actualSha256 = BpmnUtils.calculateSha256(testFile);
        assertEquals(expectedSha256, actualSha256);
    }

    @Test
    void testEncodeToBase64() {
        byte[] inputArray = new byte[]{1, 2, 3};
        byte[] encodedArray = BpmnUtils.encodeToBase64(inputArray);
        assertEquals("AQID", new String(encodedArray));
    }

    @Test
    void testToSha256ByteArray() throws NoSuchAlgorithmException, IOException {
        byte[] sha256Array = BpmnUtils.toSha256ByteArray(testFile);
        assertTrue(Arrays.equals(sha256Array, BpmnUtils.toSha256ByteArray(testFile)));
    }

    @Test
    void testBase64ToByteArray() {
        String base64 = "AQID";
        byte[] byteArray = BpmnUtils.base64ToByteArray(base64);
        assertTrue(Arrays.equals(new byte[]{1, 2, 3}, byteArray));
    }

    @Test
    void testToHexString() {
        byte[] hash = new byte[]{10, 15, 0, 125, 127, 1};
        String hexString = BpmnUtils.toHexString(hash);
        assertEquals("00000000000000000000000000000000000000000000000000000a0f007d7f01", hexString);
    }

    @Test
    void testByteArrayToString() {
        byte[] byteArray = "Test String".getBytes();
        String str = BpmnUtils.byteArrayToString(byteArray);
        assertEquals("Test String", str);
    }

    @Test
    void testExtractBpmnUUIDFromAssociations() {
        UUID uuid = UUID.randomUUID();
        BpmnBankConfig config1 = new BpmnBankConfig();
        config1.setBpmnBankConfigPK(new BpmnBankConfigPK(uuid, 1L, "acquirer1", "branch1", "terminal1"));
        BpmnBankConfig config2 = new BpmnBankConfig();
        config2.setBpmnBankConfigPK(new BpmnBankConfigPK(uuid, 2L, "acquirer2", "branch2", "terminal2"));
        List<BpmnBankConfig> associations = List.of(config1, config2);
        assertEquals(2, BpmnUtils.extractBpmnUUIDFromAssociations(associations).size());
    }

    @Test
    void testGetAcquirerConfigs() {
        UUID uuid = UUID.randomUUID();
        BpmnAssociationDto bpmnAssociationDto = mock(BpmnAssociationDto.class);
        String acquirerId = "acquirer1";
        FunctionTypeEnum functionTypeEnum = FunctionTypeEnum.MENU;
        BpmnAssociationDto emptyBpmnAssociationDto = new BpmnAssociationDto();
        assertEquals(1, BpmnUtils.getAcquirerConfigs(emptyBpmnAssociationDto, acquirerId, functionTypeEnum).size());
        emptyBpmnAssociationDto.setDefaultTemplateId(uuid);
        emptyBpmnAssociationDto.setDefaultTemplateVersion(1L);
        assertEquals(1, BpmnUtils.getAcquirerConfigs(emptyBpmnAssociationDto, acquirerId, functionTypeEnum).size());
        BranchConfigs branchConfig = new BranchConfigs();
        branchConfig.setBranchDefaultTemplateId(uuid);
        branchConfig.setBranchDefaultTemplateVersion(1L);
        emptyBpmnAssociationDto.setBranchesConfigs(List.of(branchConfig));
        assertEquals(2, BpmnUtils.getAcquirerConfigs(emptyBpmnAssociationDto, acquirerId, functionTypeEnum).size());
        TerminalConfigs terminalConfig = new TerminalConfigs();
        terminalConfig.setTemplateId(uuid);
        terminalConfig.setTemplateVersion(1L);
        terminalConfig.setTerminalIds(List.of("terminal1", "terminal2"));
        branchConfig.setTerminals(List.of(terminalConfig));
        assertEquals(4, BpmnUtils.getAcquirerConfigs(emptyBpmnAssociationDto, acquirerId, functionTypeEnum).size());
    }


    @Test
    void testGetBpmnBankConfigPKOK() {
        UUID uuid = UUID.randomUUID();
        BpmnAssociationDto bpmnAssociationDto = new BpmnAssociationDto();
        String acquirerId = "acquirer1";
        BranchConfigs branchConfig = new BranchConfigs();
        branchConfig.setBranchDefaultTemplateId(uuid);
        branchConfig.setBranchDefaultTemplateVersion(1L);
        branchConfig.setBranchId("branch1");
        Optional<BpmnBankConfigPK> optionalBpmnBankConfigPK = BpmnUtils.getBpmnBankConfigPK(bpmnAssociationDto, acquirerId, branchConfig);
        assertTrue(optionalBpmnBankConfigPK.isPresent());
        BpmnBankConfigPK bpmnBankConfigPK = optionalBpmnBankConfigPK.get();
        assertEquals(uuid, bpmnBankConfigPK.getBpmnId());
        assertEquals(1L, bpmnBankConfigPK.getBpmnModelVersion());
        assertEquals(acquirerId, bpmnBankConfigPK.getAcquirerId());
        assertEquals("branch1", bpmnBankConfigPK.getBranchId());
        assertEquals("ALL", bpmnBankConfigPK.getTerminalId());
    }

    @Test
    void testGetBpmnBankConfigPKKO() {
        UUID uuid = UUID.randomUUID();
        BpmnAssociationDto bpmnAssociationDto = new BpmnAssociationDto();
        String acquirerId = "acquirer1";
        BranchConfigs branchConfig = new BranchConfigs();
        branchConfig.setBranchId("branch1");
        Optional<BpmnBankConfigPK> optionalBpmnBankConfigPK = BpmnUtils.getBpmnBankConfigPK(bpmnAssociationDto, acquirerId, branchConfig);
        assertTrue(optionalBpmnBankConfigPK.isEmpty());
    }
}

