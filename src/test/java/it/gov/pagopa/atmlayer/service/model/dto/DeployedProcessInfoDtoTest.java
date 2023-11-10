package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class DeployedProcessInfoDtoTest {

  @Test
  void testConstructor() {
    DeployedProcessInfoDto actualDeployedProcessInfoDto = new DeployedProcessInfoDto();
    actualDeployedProcessInfoDto.setCategory("Category");
    UUID randomUUIDResult = UUID.randomUUID();
    actualDeployedProcessInfoDto.setDeploymentId(randomUUIDResult);
    actualDeployedProcessInfoDto.setDescription("The characteristics of someone or something");
    actualDeployedProcessInfoDto.setDiagram("Diagram");
    actualDeployedProcessInfoDto.setHistoryTimeToLeave(1);
    actualDeployedProcessInfoDto.setId("42");
    actualDeployedProcessInfoDto.setKey("Key");
    actualDeployedProcessInfoDto.setName("Name");
    actualDeployedProcessInfoDto.setResource("Resource");
    actualDeployedProcessInfoDto.setStartableInTasklist(true);
    actualDeployedProcessInfoDto.setSuspended(true);
    actualDeployedProcessInfoDto.setTenantId("42");
    actualDeployedProcessInfoDto.setVersion(1);
    actualDeployedProcessInfoDto.setVersionTag("1.0.2");
    assertEquals("Category", actualDeployedProcessInfoDto.getCategory());
    assertSame(randomUUIDResult, actualDeployedProcessInfoDto.getDeploymentId());
    assertEquals("The characteristics of someone or something",
        actualDeployedProcessInfoDto.getDescription());
    assertEquals("Diagram", actualDeployedProcessInfoDto.getDiagram());
    assertEquals(1, actualDeployedProcessInfoDto.getHistoryTimeToLeave());
    assertEquals("42", actualDeployedProcessInfoDto.getId());
    assertEquals("Key", actualDeployedProcessInfoDto.getKey());
    assertEquals("Name", actualDeployedProcessInfoDto.getName());
    assertEquals("Resource", actualDeployedProcessInfoDto.getResource());
    assertTrue(actualDeployedProcessInfoDto.getStartableInTasklist());
    assertTrue(actualDeployedProcessInfoDto.getSuspended());
    assertEquals("42", actualDeployedProcessInfoDto.getTenantId());
    assertEquals(1, actualDeployedProcessInfoDto.getVersion().intValue());
    assertEquals("1.0.2", actualDeployedProcessInfoDto.getVersionTag());
  }
}

