package it.gov.pagopa.atmlayer.service.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@QuarkusTest
public class ResourceFileTest {

  @Mock
  private BpmnVersion bpmnVersionMock;

  @InjectMocks
  private ResourceFile resourceFile;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    resourceFile = ResourceFile.builder()
        .id(UUID.randomUUID())
        .resourceType(S3ResourceTypeEnum.BPMN)
        .storageKey("test-key")
        .build();
  }

  @Test
  void setId() {
    UUID newId = UUID.randomUUID();
    resourceFile.setId(newId);
    assertEquals(newId, resourceFile.getId());
  }

  @Test
  void setResourceType() {
    resourceFile.setResourceType(S3ResourceTypeEnum.DMN);
    assertEquals(S3ResourceTypeEnum.DMN, resourceFile.getResourceType());
  }

  @Test
  void onPrePersist_ExtensionIsSet() {
    S3ResourceTypeEnum resourceTypeEnum = S3ResourceTypeEnum.BPMN;
    resourceFile.setResourceType(resourceTypeEnum);
    resourceFile.onPrePersist();
    assertEquals(resourceTypeEnum.getExtension(), resourceFile.getExtension());
  }

  @Test
  void createdByAndLastUpdatedBy_Set() {
    assertNull(resourceFile.getCreatedBy());
    assertNull(resourceFile.getLastUpdatedBy());
    resourceFile.setCreatedBy("user1");
    resourceFile.setLastUpdatedBy("user2");
    assertEquals("user1", resourceFile.getCreatedBy());
    assertEquals("user2", resourceFile.getLastUpdatedBy());
  }

  @Test
  void bpmnAssociation_Set() {
    resourceFile.setBpmn(bpmnVersionMock);
    assertEquals(bpmnVersionMock, resourceFile.getBpmn());
  }

  @Test
  void onPrePersist() {
    S3ResourceTypeEnum resourceTypeEnum = S3ResourceTypeEnum.BPMN;
    resourceFile.setResourceType(resourceTypeEnum);
    resourceFile.onPrePersist();
    assertEquals(resourceTypeEnum.getExtension(), resourceFile.getExtension());
  }
}


