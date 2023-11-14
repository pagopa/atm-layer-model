package it.gov.pagopa.atmlayer.service.model.model;

import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class BpmnDTOTest {

  @Test
  public void testGetterAndSetter() {
    BpmnDTO bpmnDTO = new BpmnDTO();


    bpmnDTO.setBpmnId(UUID.randomUUID());
    bpmnDTO.setModelVersion(1L);
    bpmnDTO.setDeployedFileName("example.bpmn");
    bpmnDTO.setDefinitionKey("process_key");
    bpmnDTO.setFunctionType("MENU");
    bpmnDTO.setStatus(StatusEnum.CREATED);
    bpmnDTO.setSha256("hash123");
    bpmnDTO.setDefinitionVersionCamunda(2);
    bpmnDTO.setCamundaDefinitionId("camunda123");
    bpmnDTO.setDescription("BPMN Description");
    bpmnDTO.setResourceFile(new ResourceFileDTO());
    bpmnDTO.setResource("Resource Content");
    bpmnDTO.setDeploymentId(UUID.randomUUID());
    bpmnDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    bpmnDTO.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
    bpmnDTO.setCreatedBy("User1");
    bpmnDTO.setLastUpdatedBy("User2");


    assertEquals(bpmnDTO.getBpmnId(), bpmnDTO.getBpmnId());
    assertEquals(bpmnDTO.getModelVersion(), 1L);
    assertEquals(bpmnDTO.getDeployedFileName(), "example.bpmn");
    assertEquals(bpmnDTO.getDefinitionKey(), "process_key");
    assertEquals(bpmnDTO.getFunctionType(), "MENU");
    assertEquals(bpmnDTO.getStatus(), StatusEnum.CREATED);
    assertEquals(bpmnDTO.getSha256(), "hash123");
    assertEquals(bpmnDTO.getDefinitionVersionCamunda(), 2);
    assertEquals(bpmnDTO.getCamundaDefinitionId(), "camunda123");
    assertEquals(bpmnDTO.getDescription(), "BPMN Description");
    assertEquals(bpmnDTO.getResourceFile(), new ResourceFileDTO());
    assertEquals(bpmnDTO.getResource(), "Resource Content");
    assertEquals(bpmnDTO.getDeploymentId(), bpmnDTO.getDeploymentId());
    assertNotNull(bpmnDTO.getCreatedAt());
    assertNotNull(bpmnDTO.getLastUpdatedAt());
    assertEquals(bpmnDTO.getCreatedBy(), "User1");
    assertEquals(bpmnDTO.getLastUpdatedBy(), "User2");
  }

  @Test
  public void testNoArgsConstructor() {
    BpmnDTO bpmnDTO = new BpmnDTO();
    assertNotNull(bpmnDTO);
  }

  @Test
  public void testAllArgsConstructor() {
    UUID bpmnId = UUID.randomUUID();
    Long modelVersion = 2L;
    BpmnDTO bpmnDTO = new BpmnDTO(bpmnId, modelVersion, "file.bpmn", "key123",
            "SPONTANEOUS_PAYMENT", StatusEnum.DEPLOYED, "hash456", 3,
        "camunda456", "BPMN Description", new ResourceFileDTO(),
        "Resource Content", UUID.randomUUID(), new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis()), "User3", "User4");

    assertEquals(bpmnDTO.getBpmnId(), bpmnId);
    assertEquals(bpmnDTO.getModelVersion(), modelVersion);
  }

  @Test
  public void testBuilder() {
    BpmnDTO bpmnDTO = BpmnDTO.builder()
        .bpmnId(UUID.randomUUID())
        .modelVersion(3L)
        .deployedFileName("file3.bpmn")
        .definitionKey("key789")
        .functionType("SPONTANEOUS_PAYMENT")
        .status(StatusEnum.CREATED)
        .sha256("hash789")
        .definitionVersionCamunda(4)
        .camundaDefinitionId("camunda789")
        .description("BPMN Description 3")
        .resourceFile(new ResourceFileDTO())
        .resource("Resource Content 3")
        .deploymentId(UUID.randomUUID())
        .createdAt(new Timestamp(System.currentTimeMillis()))
        .lastUpdatedAt(new Timestamp(System.currentTimeMillis()))
        .createdBy("User5")
        .lastUpdatedBy("User6")
        .build();

    assertNotNull(bpmnDTO);
  }

  @Test
  public void testToString() {
    BpmnDTO bpmnDTO = BpmnDTO.builder()
        .bpmnId(UUID.randomUUID())
        .modelVersion(4L)
        .deployedFileName("file4.bpmn")
        .definitionKey("key999")
        .functionType("SPONTANEOUS_PAYMENT")
        .status(StatusEnum.CREATED)
        .sha256("hash999")
        .definitionVersionCamunda(5)
        .camundaDefinitionId("camunda999")
        .description("BPMN Description 4")
        .resourceFile(new ResourceFileDTO())
        .resource("Resource Content 4")
        .deploymentId(UUID.randomUUID())
        .createdAt(new Timestamp(System.currentTimeMillis()))
        .lastUpdatedAt(new Timestamp(System.currentTimeMillis()))
        .createdBy("User7")
        .lastUpdatedBy("User8")
        .build();

    String expectedToString = "BpmnDTO(bpmnId=" + bpmnDTO.getBpmnId() +
        ", modelVersion=4, deployedFileName=file4.bpmn, definitionKey=key999, " +
        "functionType=SPONTANEOUS_PAYMENT, status=CREATED, sha256=hash999, " +
        "definitionVersionCamunda=5, camundaDefinitionId=camunda999, " +
        "description=BPMN Description 4, resourceFile=ResourceFileDTO(id=null, resourceType=null, storageKey=null, fileName=null, extension=null, createdAt=null, lastUpdatedAt=null, createdBy=null, lastUpdatedBy=null), " +
        "resource=Resource Content 4, deploymentId=" + bpmnDTO.getDeploymentId() +
        ", createdAt=" + bpmnDTO.getCreatedAt() + ", lastUpdatedAt=" + bpmnDTO.getLastUpdatedAt() +
        ", createdBy=User7, lastUpdatedBy=User8)";

    assertEquals(expectedToString, bpmnDTO.toString());
  }

  @Test
  public void testEqualsAndHashCode() {
    BpmnDTO bpmnDTO1 = new BpmnDTO(UUID.randomUUID(), 1L, "file1.bpmn", "key1",
        "MENU", StatusEnum.CREATED, "hash1", 2,
        "camunda1", "BPMN Description 1", new ResourceFileDTO(),
        "Resource Content 1", UUID.randomUUID(), new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis()), "User1", "User2");

    BpmnDTO bpmnDTO2 = new BpmnDTO(UUID.randomUUID(), 1L, "file1.bpmn", "key1",
            "MENU", StatusEnum.CREATED, "hash1", 2,
        "camunda1", "BPMN Description 1", new ResourceFileDTO(),
        "Resource Content 1", UUID.randomUUID(), new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis()), "User1", "User2");

    assertEquals(bpmnDTO1, bpmnDTO1);
    assertNotEquals(bpmnDTO1, bpmnDTO2);

    assertNotEquals(bpmnDTO1, null);

    BpmnDTO bpmnDTO3 = new BpmnDTO(UUID.randomUUID(), 3L, "file3.bpmn", "key3",
            "MENU", StatusEnum.DEPLOYED, "hash3", 4,
        "camunda3", "BPMN Description 3", new ResourceFileDTO(),
        "Resource Content 3", UUID.randomUUID(), new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis()), "User3", "User4");

    assertNotEquals(bpmnDTO1, bpmnDTO3);
  }
}
