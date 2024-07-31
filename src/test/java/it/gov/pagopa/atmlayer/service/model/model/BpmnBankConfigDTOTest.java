package it.gov.pagopa.atmlayer.service.model.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
class BpmnBankConfigDTOTest {
    @Test
    void testNoArgsConstructor() {
        BpmnBankConfigDTO dto = new BpmnBankConfigDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        BpmnBankConfigDTO dto = new BpmnBankConfigDTO(
                UUID.randomUUID(), 1L, "acquirer", "branch", "terminal",
                "MENU", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), "creator", "updator");
        assertNotNull(dto);
    }

    @Test
    void testGetterAndSetter() {
        BpmnBankConfigDTO dto = new BpmnBankConfigDTO();

        dto.setBpmnId(UUID.randomUUID());
        assertNotEquals(dto.getBpmnId(), UUID.randomUUID());

        dto.setBpmnModelVersion(1L);
        assertEquals(1L, dto.getBpmnModelVersion());

        dto.setAcquirerId("acquirer");
        assertEquals("acquirer", dto.getAcquirerId());

        dto.setBranchId("branch");
        assertEquals("branch", dto.getBranchId());

        dto.setTerminalId("terminal");
        assertEquals("terminal", dto.getTerminalId());

        dto.setFunctionType("MENU");
        assertEquals("MENU", dto.getFunctionType());

        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        dto.setCreatedAt(createdAt);
        assertEquals(dto.getCreatedAt(), createdAt);

        Timestamp lastUpdatedAt = new Timestamp(System.currentTimeMillis());
        dto.setLastUpdatedAt(lastUpdatedAt);
        assertEquals(dto.getLastUpdatedAt(), lastUpdatedAt);

        dto.setCreatedBy("creator");
        assertEquals("creator", dto.getCreatedBy());

        dto.setLastUpdatedBy("updator");
        assertEquals("updator", dto.getLastUpdatedBy());
    }

    @Test
    void testEqualsAndHashCode() {
        BpmnBankConfigDTO dto1 = new BpmnBankConfigDTO(
                UUID.randomUUID(), 1L, "acquirer", "branch", "terminal",
                "MENU", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), "creator", "updator");
        BpmnBankConfigDTO dto2 = new BpmnBankConfigDTO(
                UUID.randomUUID(), 1L, "acquirer", "branch", "terminal",
                "MENU", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), "creator", "updator");

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        BpmnBankConfigDTO dto = new BpmnBankConfigDTO(
                UUID.randomUUID(), 1L, "acquirer", "branch", "terminal",
                "MENU", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), "creator", "updator");

        String expectedToString = "BpmnBankConfigDTO(bpmnId=" + dto.getBpmnId() +
                ", bpmnModelVersion=" + dto.getBpmnModelVersion() +
                ", acquirerId=" + dto.getAcquirerId() +
                ", branchId=" + dto.getBranchId() +
                ", terminalId=" + dto.getTerminalId() +
                ", functionType=" + dto.getFunctionType() +
                ", createdAt=" + dto.getCreatedAt() +
                ", lastUpdatedAt=" + dto.getLastUpdatedAt() +
                ", createdBy=" + dto.getCreatedBy() +
                ", lastUpdatedBy=" + dto.getLastUpdatedBy() + ")";

        assertEquals(expectedToString, dto.toString());
    }
}
