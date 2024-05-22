package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class WorkflowResourceMapperTest {
  private WorkflowResourceMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(WorkflowResourceMapper.class);
  }

  @Test
  void testToEntityCreation() throws NoSuchAlgorithmException, IOException {

    WorkflowResourceCreationDto creationDto = mock(WorkflowResourceCreationDto.class);
    File tempFile = createTemporaryFileWithContent();

    when(creationDto.getFile()).thenReturn(tempFile);
    when(creationDto.getFilename()).thenReturn("testFile");
    when(creationDto.getResourceType()).thenReturn(DeployableResourceType.DMN);

    String expectedSha256 = FileUtilities.calculateSha256(tempFile);

    WorkflowResource workflowResource = mapper.toEntityCreation(creationDto);

    assertNotNull(workflowResource);
    assertEquals(StatusEnum.CREATED, workflowResource.getStatus());
    assertEquals(expectedSha256, workflowResource.getSha256());
    assertEquals("testFile.DMN", workflowResource.getDeployedFileName());
  }

  @Test
  void toDto_null(){
    WorkflowResource resourceFile = null;
    WorkflowResourceDTO resource = mapper.toDTO(resourceFile);
    assertNull(resource);
  }
  @Test
  void toDtoCreation_null(){
    WorkflowResourceCreationDto resourceFile = null;
    WorkflowResourceCreationDto resource = mapper.toDtoCreation(resourceFile);
    assertNull(resource);
  }

  @Test
  void toDtoCreation(){
    WorkflowResourceCreationDto resourceFile = new WorkflowResourceCreationDto();;
    resourceFile.setResourceType(DeployableResourceType.BPMN);
    WorkflowResourceCreationDto resource = mapper.toDtoCreation(resourceFile);
    assertNotNull(resource);
    assertEquals(DeployableResourceType.BPMN, resource.getResourceType());
  }

  private File createTemporaryFileWithContent() throws IOException {
    Path tempFilePath = Files.createTempFile("temp-file", ".txt");
    Files.write(tempFilePath, "file content".getBytes());
    return tempFilePath.toFile();
  }

  @Test
  void testToDTOList() {

    List<WorkflowResource> workflowResourceList = Arrays.asList(
        createWorkflowResource("file1", "BPMN"),
        createWorkflowResource("file2", "DMN")
    );

    List<WorkflowResourceDTO> dtoList = mapper.toDTOList(workflowResourceList);

    assertNotNull(dtoList);
    assertEquals(workflowResourceList.size(), dtoList.size());
  }

  private WorkflowResource createWorkflowResource(String filename, String resourceType) {
    WorkflowResource workflowResource = new WorkflowResource();
    workflowResource.setDeployedFileName(filename);
    workflowResource.setResourceType(DeployableResourceType.valueOf(resourceType));
    return workflowResource;
  }
}
