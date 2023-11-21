package it.gov.pagopa.atmlayer.service.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
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

public class ResourceEntityMapperTest {

  private ResourceEntityMapper mapper;
  private ResourceEntityStorageService storageService;

  @BeforeEach
  public void setUp() {
    storageService = mock(ResourceEntityStorageService.class);
    mapper = new ResourceEntityMapperImpl();
    mapper.resourceEntityStorageService = storageService;
  }

  @Test
  public void testToEntityCreation() throws NoSuchAlgorithmException, IOException {

    ResourceCreationDto creationDto = mock(ResourceCreationDto.class);
    File tempFile = createTemporaryFileWithContent("file content");

    when(creationDto.getFile()).thenReturn(tempFile);
    when(creationDto.getFilename()).thenReturn("testFile");
    when(creationDto.getResourceType()).thenReturn(NoDeployableResourceType.HTML);
    when(creationDto.getPath()).thenReturn("/path/to/resource");
    when(storageService.calculateStorageKey(NoDeployableResourceType.valueOf("HTML"),
        "/path/to/resource", "testFile"))
        .thenReturn("EXPECTED_STORAGE_KEY");

    String expectedSha256 = FileUtilities.calculateSha256(tempFile);

    ResourceEntity resourceEntity = mapper.toEntityCreation(creationDto);

    assertNotNull(resourceEntity);
    assertEquals("html", resourceEntity.getNoDeployableResourceType().getExtension());
    assertEquals(expectedSha256, resourceEntity.getSha256());
    assertEquals("testFile", resourceEntity.getFileName());
    assertEquals("EXPECTED_STORAGE_KEY", resourceEntity.getStorageKey());

    verify(storageService, times(1)).calculateStorageKey(NoDeployableResourceType.valueOf("HTML"),
        "/path/to/resource", "testFile");
  }

  private File createTemporaryFileWithContent(String content) throws IOException {
    Path tempFilePath = Files.createTempFile("temp-file", ".txt");
    Files.write(tempFilePath, content.getBytes());
    return tempFilePath.toFile();
  }

  @Test
  public void testToDTOList() {

    mapper = Mappers.getMapper(ResourceEntityMapper.class);

    ResourceFile resourceFile = new ResourceFile();
    resourceFile.setStorageKey("StorageKey");

    List<ResourceEntity> resourceEntityList = Arrays.asList(
        createResourceEntity("file1", "HTML",resourceFile),
        createResourceEntity("file2", "OTHER",resourceFile)
    );

    List<ResourceDTO> dtoList = mapper.toDTOList(resourceEntityList);

    assertNotNull(dtoList);
    assertEquals(resourceEntityList.size(), dtoList.size());
  }

  private ResourceEntity createResourceEntity(String filename, String noDeployableResourceType, ResourceFile resourceFile) {
    ResourceEntity resourceEntity = new ResourceEntity();
    resourceEntity.setFileName(filename);
    resourceEntity.setResourceFile(resourceFile);
    resourceEntity.setNoDeployableResourceType(
        NoDeployableResourceType.valueOf(noDeployableResourceType));
    return resourceEntity;
  }
}
