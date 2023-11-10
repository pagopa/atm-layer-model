package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class DeployResponseDtoTest {

  @Test
  void testNoArgsConstructor() {
    DeployResponseDto actualDeployResponseDto = new DeployResponseDto();
    actualDeployResponseDto.setDeployedCaseDefinitions("Deployed Case Definitions");
    actualDeployResponseDto.setDeployedDecisionDefinitions("Deployed Decision Definitions");
    actualDeployResponseDto.setDeployedDecisionRequirementsDefinitions(
        "Deployed Decision Requirements Definitions");
    HashMap<String, DeployedProcessInfoDto> stringDeployedProcessInfoDtoMap = new HashMap<>();
    actualDeployResponseDto.setDeployedProcessDefinitions(stringDeployedProcessInfoDtoMap);
    actualDeployResponseDto.setDeploymentTime("Deployment Time");
    UUID randomUUIDResult = UUID.randomUUID();
    actualDeployResponseDto.setId(randomUUIDResult);
    ArrayList<LinkDto> linkDtoList = new ArrayList<>();
    actualDeployResponseDto.setLinks(linkDtoList);
    actualDeployResponseDto.setName("Name");
    actualDeployResponseDto.setSource("Source");
    actualDeployResponseDto.setTenantId("42");
    assertEquals("Deployed Case Definitions", actualDeployResponseDto.getDeployedCaseDefinitions());
    assertEquals("Deployed Decision Definitions",
        actualDeployResponseDto.getDeployedDecisionDefinitions());
    assertEquals("Deployed Decision Requirements Definitions",
        actualDeployResponseDto.getDeployedDecisionRequirementsDefinitions());
    assertSame(stringDeployedProcessInfoDtoMap,
        actualDeployResponseDto.getDeployedProcessDefinitions());
    assertEquals("Deployment Time", actualDeployResponseDto.getDeploymentTime());
    assertSame(randomUUIDResult, actualDeployResponseDto.getId());
    assertSame(linkDtoList, actualDeployResponseDto.getLinks());
    assertEquals("Name", actualDeployResponseDto.getName());
    assertEquals("Source", actualDeployResponseDto.getSource());
    assertEquals("42", actualDeployResponseDto.getTenantId());
  }

  @Test
  void testAllArgsConstructor() {
    ArrayList<LinkDto> linkDtoList = new ArrayList<>();
    UUID id = UUID.randomUUID();
    DeployResponseDto actualDeployResponseDto = new DeployResponseDto(linkDtoList, id, "Name",
        "Source",
        "Deployment Time", "42", new HashMap<>(), "Deployed Case Definitions",
        "Deployed Decision Definitions",
        "Deployed Decision Requirements Definitions");
    actualDeployResponseDto.setDeployedCaseDefinitions("Deployed Case Definitions");
    actualDeployResponseDto.setDeployedDecisionDefinitions("Deployed Decision Definitions");
    actualDeployResponseDto.setDeployedDecisionRequirementsDefinitions(
        "Deployed Decision Requirements Definitions");
    HashMap<String, DeployedProcessInfoDto> stringDeployedProcessInfoDtoMap = new HashMap<>();
    actualDeployResponseDto.setDeployedProcessDefinitions(stringDeployedProcessInfoDtoMap);
    actualDeployResponseDto.setDeploymentTime("Deployment Time");
    UUID randomUUIDResult = UUID.randomUUID();
    actualDeployResponseDto.setId(randomUUIDResult);
    ArrayList<LinkDto> linkDtoList1 = new ArrayList<>();
    actualDeployResponseDto.setLinks(linkDtoList1);
    actualDeployResponseDto.setName("Name");
    actualDeployResponseDto.setSource("Source");
    actualDeployResponseDto.setTenantId("42");
    assertEquals("Deployed Case Definitions", actualDeployResponseDto.getDeployedCaseDefinitions());
    assertEquals("Deployed Decision Definitions",
        actualDeployResponseDto.getDeployedDecisionDefinitions());
    assertEquals("Deployed Decision Requirements Definitions",
        actualDeployResponseDto.getDeployedDecisionRequirementsDefinitions());
    assertSame(stringDeployedProcessInfoDtoMap,
        actualDeployResponseDto.getDeployedProcessDefinitions());
    assertEquals("Deployment Time", actualDeployResponseDto.getDeploymentTime());
    assertSame(randomUUIDResult, actualDeployResponseDto.getId());
    List<LinkDto> links = actualDeployResponseDto.getLinks();
    assertSame(linkDtoList1, links);
    assertEquals(linkDtoList, links);
    assertEquals("Name", actualDeployResponseDto.getName());
    assertEquals("Source", actualDeployResponseDto.getSource());
    assertEquals("42", actualDeployResponseDto.getTenantId());
  }
}

