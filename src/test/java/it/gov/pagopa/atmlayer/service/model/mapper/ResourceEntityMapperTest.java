package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class ResourceEntityMapperTest {

    private ResourceEntityMapper mapper;
    private ResourceEntityStorageService storageService;

    @BeforeEach
    public void setUp() {
        storageService = mock(ResourceEntityStorageService.class);
        mapper = new ResourceEntityMapperImpl();
        mapper.resourceEntityStorageService = storageService;
    }

    @Test
    void testToEntityCreation() throws NoSuchAlgorithmException, IOException {

        ResourceCreationDto creationDto = mock(ResourceCreationDto.class);
        File tempFile = createTemporaryFileWithContent();

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

    private File createTemporaryFileWithContent() throws IOException {
        Path tempFilePath = Files.createTempFile("temp-file", ".txt");
        Files.write(tempFilePath, "file content".getBytes());
        return tempFilePath.toFile();
    }

    @Test
    void testToDTOList() {

        mapper = Mappers.getMapper(ResourceEntityMapper.class);

        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setStorageKey("StorageKey");

        List<ResourceEntity> resourceEntityList = Arrays.asList(
                createResourceEntity("file1", "HTML", resourceFile),
                createResourceEntity("file2", "OTHER", resourceFile)
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

    @Test
    void toDTOTest_null() {
        ResourceEntity resourceFile = null;
        ResourceDTO resource = mapper.toDTO(resourceFile);
        assertNull(resource);
    }

}
