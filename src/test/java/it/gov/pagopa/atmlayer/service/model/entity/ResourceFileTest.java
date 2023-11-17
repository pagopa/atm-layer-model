package it.gov.pagopa.atmlayer.service.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import java.sql.Timestamp;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.junit.jupiter.api.Test;

class ResourceFileTest {

  @Test
  void resourceFileEntityTest() throws NoSuchFieldException {
    ResourceFile resourceFile = new ResourceFile();

    Entity entityAnnotation = resourceFile.getClass().getAnnotation(Entity.class);
    assertNotNull(entityAnnotation);

    Table tableAnnotation = resourceFile.getClass().getAnnotation(Table.class);
    assertNotNull(tableAnnotation);
    assertEquals("resource_file_model", tableAnnotation.name());

    assertNotNull(resourceFile.getClass().getDeclaredField("id").getAnnotation(Id.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("id").getAnnotation(GeneratedValue.class));
    assertNotNull(resourceFile.getClass().getDeclaredField("id").getAnnotation(Column.class));

    assertNotNull(
        resourceFile.getClass().getDeclaredField("resourceType").getAnnotation(Column.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("resourceType").getAnnotation(Enumerated.class));

    assertNotNull(resourceFile.getClass().getDeclaredField("bpmn").getAnnotation(OneToOne.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("bpmn").getAnnotation(JoinColumns.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("workflowResource").getAnnotation(OneToOne.class));
    assertNotNull(resourceFile.getClass().getDeclaredField("workflowResource")
        .getAnnotation(JoinColumns.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("resourceEntity").getAnnotation(OneToOne.class));
    assertNotNull(resourceFile.getClass().getDeclaredField("resourceEntity").getAnnotation(
        JoinColumns.class));

    assertNotNull(
        resourceFile.getClass().getDeclaredField("storageKey").getAnnotation(Column.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("storageKey").getAnnotation(NotNull.class));

    assertNotNull(resourceFile.getClass().getDeclaredField("createdAt").getAnnotation(
        CreationTimestamp.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("createdAt").getAnnotation(Column.class));

    assertNotNull(resourceFile.getClass().getDeclaredField("lastUpdatedAt").getAnnotation(
        UpdateTimestamp.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("lastUpdatedAt").getAnnotation(Column.class));

    assertNotNull(
        resourceFile.getClass().getDeclaredField("createdBy").getAnnotation(Column.class));
    assertNotNull(
        resourceFile.getClass().getDeclaredField("lastUpdatedBy").getAnnotation(Column.class));
  }


  @Test
  public void testOnPrePersist3() {
    UUID id = UUID.randomUUID();
    BpmnVersion bpmn = new BpmnVersion();
    WorkflowResource workflowResource = new WorkflowResource();
    ResourceFile resourceFile = new ResourceFile(id, S3ResourceTypeEnum.BPMN, bpmn,
        workflowResource,
        new ResourceEntity(), "Storage Key", "foo.txt", "Extension", mock(Timestamp.class),
        mock(Timestamp.class),
        "Jan 1, 2020 8:00am GMT+0100", "2020-03-01");
    resourceFile.onPrePersist();
    assertEquals("bpmn", resourceFile.getExtension());
  }
}

