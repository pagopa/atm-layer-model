package it.gov.pagopa.atmlayer.service.model.model;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class BpmnBankConfigDTOTest {
  @Test
  public void testNoArgsConstructor() {
    BpmnBankConfigDTO dto = new BpmnBankConfigDTO();
    assertNotNull(dto);
  }

  @Test
  public void testAllArgsConstructor() {
    BpmnBankConfigDTO dto = new BpmnBankConfigDTO(
        UUID.randomUUID(), 1L, "acquirer", "branch", "terminal",
        "MENU", new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis()), "creator", "updator");
    assertNotNull(dto);
  }

  @Test
  public void testGetterAndSetter() {
    BpmnBankConfigDTO dto = new BpmnBankConfigDTO();

    dto.setBpmnId(UUID.randomUUID());
    assertNotEquals(dto.getBpmnId(), UUID.randomUUID());

    dto.setBpmnModelVersion(1L);
    assertEquals(dto.getBpmnModelVersion(), 1L);

    dto.setAcquirerId("acquirer");
    assertEquals(dto.getAcquirerId(), "acquirer");

    dto.setBranchId("branch");
    assertEquals(dto.getBranchId(), "branch");

    dto.setTerminalId("terminal");
    assertEquals(dto.getTerminalId(), "terminal");

    dto.setFunctionType("MENU");
    assertEquals(dto.getFunctionType(), "MENU");

    Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    dto.setCreatedAt(createdAt);
    assertEquals(dto.getCreatedAt(), createdAt);

    Timestamp lastUpdatedAt = new Timestamp(System.currentTimeMillis());
    dto.setLastUpdatedAt(lastUpdatedAt);
    assertEquals(dto.getLastUpdatedAt(), lastUpdatedAt);

    dto.setCreatedBy("creator");
    assertEquals(dto.getCreatedBy(), "creator");

    dto.setLastUpdatedBy("updator");
    assertEquals(dto.getLastUpdatedBy(), "updator");
  }

  @Test
  public void testEqualsAndHashCode() {
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
  public void testToString() {
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
