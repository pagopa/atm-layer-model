package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class BpmnBankConfigTest {

  private BpmnBankConfig bpmnBankConfig;

  @BeforeEach
  public void setUp() {
    bpmnBankConfig = new BpmnBankConfig();
  }

  @Test
  public void testGettersAndSetters() {

    BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
    bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
    assertEquals(bpmnBankConfigPK, bpmnBankConfig.getBpmnBankConfigPK());

    FunctionTypeEnum functionType = FunctionTypeEnum.MENU;
    bpmnBankConfig.setFunctionType(functionType);
    assertEquals(functionType, bpmnBankConfig.getFunctionType());

    Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    bpmnBankConfig.setCreatedAt(createdAt);
    assertEquals(createdAt, bpmnBankConfig.getCreatedAt());

    Timestamp lastUpdatedAt = new Timestamp(System.currentTimeMillis());
    bpmnBankConfig.setLastUpdatedAt(lastUpdatedAt);
    assertEquals(lastUpdatedAt, bpmnBankConfig.getLastUpdatedAt());

    String createdBy = "name";
    bpmnBankConfig.setCreatedBy(createdBy);
    assertEquals(createdBy, bpmnBankConfig.getCreatedBy());

    String lastUpdatedBy = "surname";
    bpmnBankConfig.setLastUpdatedBy(lastUpdatedBy);
    assertEquals(lastUpdatedBy, bpmnBankConfig.getLastUpdatedBy());
  }
}
