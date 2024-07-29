package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DeployedBPMNProcessDefinitionDtoTest {

  @Test
  void testConstructor() {
    DeployedBPMNProcessDefinitionDto actualDeployedBPMNProcessDefinitionDto = new DeployedBPMNProcessDefinitionDto();
    actualDeployedBPMNProcessDefinitionDto.setCategory("Category");
    UUID randomUUIDResult = UUID.randomUUID();
    actualDeployedBPMNProcessDefinitionDto.setDeploymentId(randomUUIDResult);
    actualDeployedBPMNProcessDefinitionDto.setDescription("The characteristics of someone or something");
    actualDeployedBPMNProcessDefinitionDto.setDiagram("Diagram");
    actualDeployedBPMNProcessDefinitionDto.setHistoryTimeToLive(1);
    actualDeployedBPMNProcessDefinitionDto.setId("42");
    actualDeployedBPMNProcessDefinitionDto.setKey("Key");
    actualDeployedBPMNProcessDefinitionDto.setName("Name");
    actualDeployedBPMNProcessDefinitionDto.setResource("Resource");
    actualDeployedBPMNProcessDefinitionDto.setStartableInTasklist(true);
    actualDeployedBPMNProcessDefinitionDto.setSuspended(true);
    actualDeployedBPMNProcessDefinitionDto.setTenantId("42");
    actualDeployedBPMNProcessDefinitionDto.setVersion(1);
    actualDeployedBPMNProcessDefinitionDto.setVersionTag("1.0.2");
    assertEquals("Category", actualDeployedBPMNProcessDefinitionDto.getCategory());
    assertSame(randomUUIDResult, actualDeployedBPMNProcessDefinitionDto.getDeploymentId());
    assertEquals("The characteristics of someone or something",
        actualDeployedBPMNProcessDefinitionDto.getDescription());
    assertEquals("Diagram", actualDeployedBPMNProcessDefinitionDto.getDiagram());
    assertEquals(1, actualDeployedBPMNProcessDefinitionDto.getHistoryTimeToLive());
    assertEquals("42", actualDeployedBPMNProcessDefinitionDto.getId());
    assertEquals("Key", actualDeployedBPMNProcessDefinitionDto.getKey());
    assertEquals("Name", actualDeployedBPMNProcessDefinitionDto.getName());
    assertEquals("Resource", actualDeployedBPMNProcessDefinitionDto.getResource());
    assertTrue(actualDeployedBPMNProcessDefinitionDto.getStartableInTasklist());
    assertTrue(actualDeployedBPMNProcessDefinitionDto.getSuspended());
    assertEquals("42", actualDeployedBPMNProcessDefinitionDto.getTenantId());
    assertEquals(1, actualDeployedBPMNProcessDefinitionDto.getVersion().intValue());
    assertEquals("1.0.2", actualDeployedBPMNProcessDefinitionDto.getVersionTag());
  }
}

