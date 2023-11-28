package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFileDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
@QuarkusTest
class ResourceFileMapperImplTest {

    private ResourceFileMapperImpl resourceFileMapper;

    @BeforeEach
    public void setUp() {
        resourceFileMapper = new ResourceFileMapperImpl();
    }

    @Test
    void toDTOTest_null(){
        ResourceFile resourceFile = null;
        ResourceFileDTO resource = resourceFileMapper.toDTO(resourceFile);
        assertNull(resource);
    }

    @Test
    void toDTOTest(){
        ResourceFile resourceFile = new ResourceFile();
        ResourceFileDTO resource = resourceFileMapper.toDTO(resourceFile);

        assertNotNull(resource);
        assertEquals(resourceFile.getId(), resource.getId());
        assertEquals(resourceFile.getResourceType(), resource.getResourceType());
        assertEquals(resourceFile.getStorageKey(), resource.getStorageKey());
        assertEquals(resourceFile.getFileName(), resource.getFileName());
        assertEquals(resourceFile.getExtension(), resource.getExtension());
        assertEquals(resourceFile.getCreatedAt(), resource.getCreatedAt());
        assertEquals(resourceFile.getLastUpdatedAt(), resource.getLastUpdatedAt());
        assertEquals(resourceFile.getCreatedBy(), resource.getCreatedBy());
        assertEquals(resourceFile.getLastUpdatedBy(), resource.getLastUpdatedBy());
    }

    @Test
    void toEntity_null(){
        ResourceFileDTO  resourceFile = null;
        ResourceFile resource = resourceFileMapper.toEntity(resourceFile);
        assertNull(resource);
    }

    @Test
    void toEntity(){
        ResourceFileDTO resourceFile = new ResourceFileDTO();
        ResourceFile resource = resourceFileMapper.toEntity(resourceFile);
        assertNotNull(resource);
        assertEquals(resourceFile.getId(), resource.getId());
        assertEquals(resourceFile.getId(), resource.getId());
        assertEquals(resourceFile.getResourceType(), resource.getResourceType());
        assertEquals(resourceFile.getStorageKey(), resource.getStorageKey());
        assertEquals(resourceFile.getFileName(), resource.getFileName());
        assertEquals(resourceFile.getExtension(), resource.getExtension());
        assertEquals(resourceFile.getCreatedAt(), resource.getCreatedAt());
        assertEquals(resourceFile.getLastUpdatedAt(), resource.getLastUpdatedAt());
        assertEquals(resourceFile.getCreatedBy(), resource.getCreatedBy());
        assertEquals(resourceFile.getLastUpdatedBy(), resource.getLastUpdatedBy());

    }

}
