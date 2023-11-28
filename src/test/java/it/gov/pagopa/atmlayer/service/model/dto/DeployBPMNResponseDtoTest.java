package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
@QuarkusTest
public class DeployBPMNResponseDtoTest {

//  @Test
//  void testNoArgsConstructor() {
//    DeployBPMNResponseDto actualDeployBPMNResponseDto = new DeployBPMNResponseDto();
//    actualDeployBPMNResponseDto.setDeployedCaseDefinitions("Deployed Case Definitions");
//    actualDeployBPMNResponseDto.setDeployedDecisionDefinitions("Deployed Decision Definitions");
//    actualDeployBPMNResponseDto.setDeployedDecisionRequirementsDefinitions(
//        "Deployed Decision Requirements Definitions");
//    HashMap<String, DeployedBPMNProcessDefinitionDto> stringDeployedProcessInfoDtoMap = new HashMap<>();
//    actualDeployBPMNResponseDto.setDeployedProcessDefinitions(stringDeployedProcessInfoDtoMap);
//    actualDeployBPMNResponseDto.setDeploymentTime("Deployment Time");
//    UUID randomUUIDResult = UUID.randomUUID();
//    actualDeployBPMNResponseDto.setId(randomUUIDResult);
//    ArrayList<LinkDto> linkDtoList = new ArrayList<>();
//    actualDeployBPMNResponseDto.setLinks(linkDtoList);
//    actualDeployBPMNResponseDto.setName("Name");
//    actualDeployBPMNResponseDto.setSource("Source");
//    actualDeployBPMNResponseDto.setTenantId("42");
//    assertEquals("Deployed Case Definitions", actualDeployBPMNResponseDto.getDeployedCaseDefinitions());
//    assertEquals("Deployed Decision Definitions",
//        actualDeployBPMNResponseDto.getDeployedDecisionDefinitions());
//    assertEquals("Deployed Decision Requirements Definitions",
//        actualDeployBPMNResponseDto.getDeployedDecisionRequirementsDefinitions());
//    assertSame(stringDeployedProcessInfoDtoMap,
//        actualDeployBPMNResponseDto.getDeployedProcessDefinitions());
//    assertEquals("Deployment Time", actualDeployBPMNResponseDto.getDeploymentTime());
//    assertSame(randomUUIDResult, actualDeployBPMNResponseDto.getId());
//    assertSame(linkDtoList, actualDeployBPMNResponseDto.getLinks());
//    assertEquals("Name", actualDeployBPMNResponseDto.getName());
//    assertEquals("Source", actualDeployBPMNResponseDto.getSource());
//    assertEquals("42", actualDeployBPMNResponseDto.getTenantId());
//  }
//
//  @Test
//  void testAllArgsConstructor() {
//    ArrayList<LinkDto> linkDtoList = new ArrayList<>();
//    UUID id = UUID.randomUUID();
//    DeployBPMNResponseDto actualDeployBPMNResponseDto = new DeployBPMNResponseDto(linkDtoList, id, "Name",
//        "Source",
//        "Deployment Time", "42", new HashMap<>(), "Deployed Case Definitions",
//        "Deployed Decision Definitions",
//        "Deployed Decision Requirements Definitions");
//    actualDeployBPMNResponseDto.setDeployedCaseDefinitions("Deployed Case Definitions");
//    actualDeployBPMNResponseDto.setDeployedDecisionDefinitions("Deployed Decision Definitions");
//    actualDeployBPMNResponseDto.setDeployedDecisionRequirementsDefinitions(
//        "Deployed Decision Requirements Definitions");
//    HashMap<String, DeployedBPMNProcessDefinitionDto> stringDeployedProcessInfoDtoMap = new HashMap<>();
//    actualDeployBPMNResponseDto.setDeployedProcessDefinitions(stringDeployedProcessInfoDtoMap);
//    actualDeployBPMNResponseDto.setDeploymentTime("Deployment Time");
//    UUID randomUUIDResult = UUID.randomUUID();
//    actualDeployBPMNResponseDto.setId(randomUUIDResult);
//    ArrayList<LinkDto> linkDtoList1 = new ArrayList<>();
//    actualDeployBPMNResponseDto.setLinks(linkDtoList1);
//    actualDeployBPMNResponseDto.setName("Name");
//    actualDeployBPMNResponseDto.setSource("Source");
//    actualDeployBPMNResponseDto.setTenantId("42");
//    assertEquals("Deployed Case Definitions", actualDeployBPMNResponseDto.getDeployedCaseDefinitions());
//    assertEquals("Deployed Decision Definitions",
//        actualDeployBPMNResponseDto.getDeployedDecisionDefinitions());
//    assertEquals("Deployed Decision Requirements Definitions",
//        actualDeployBPMNResponseDto.getDeployedDecisionRequirementsDefinitions());
//    assertSame(stringDeployedProcessInfoDtoMap,
//        actualDeployBPMNResponseDto.getDeployedProcessDefinitions());
//    assertEquals("Deployment Time", actualDeployBPMNResponseDto.getDeploymentTime());
//    assertSame(randomUUIDResult, actualDeployBPMNResponseDto.getId());
//    List<LinkDto> links = actualDeployBPMNResponseDto.getLinks();
//    assertSame(linkDtoList1, links);
//    assertEquals(linkDtoList, links);
//    assertEquals("Name", actualDeployBPMNResponseDto.getName());
//    assertEquals("Source", actualDeployBPMNResponseDto.getSource());
//    assertEquals("42", actualDeployBPMNResponseDto.getTenantId());
//  }
}

