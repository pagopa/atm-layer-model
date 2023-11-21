package it.gov.pagopa.atmlayer.service.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class WorkflowResourceMapperTest {
  private WorkflowResourceMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(WorkflowResourceMapper.class);
  }

  @Test
  public void testToEntityCreation() throws NoSuchAlgorithmException, IOException {

    WorkflowResourceCreationDto creationDto = mock(WorkflowResourceCreationDto.class);
    File tempFile = createTemporaryFileWithContent("file content");

    when(creationDto.getFile()).thenReturn(tempFile);
    when(creationDto.getFilename()).thenReturn("testFile");
    when(creationDto.getResourceType()).thenReturn(DeployableResourceType.DMN);

    String expectedSha256 = FileUtils.calculateSha256(tempFile);

    WorkflowResource workflowResource = mapper.toEntityCreation(creationDto);

    assertNotNull(workflowResource);
    assertEquals(StatusEnum.CREATED, workflowResource.getStatus());
    assertEquals(expectedSha256, workflowResource.getSha256());
    assertEquals("testFile.DMN", workflowResource.getDeployedFileName());
  }

  private File createTemporaryFileWithContent(String content) throws IOException {
    Path tempFilePath = Files.createTempFile("temp-file", ".txt");
    Files.write(tempFilePath, content.getBytes());
    return tempFilePath.toFile();
  }

  @Test
  public void testToDTOList() {

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
