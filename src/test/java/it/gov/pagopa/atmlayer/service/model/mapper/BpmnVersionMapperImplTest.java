package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnProcessDTO;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BpmnVersionMapperImplTest {

    private BpmnVersionMapperImpl bpmnVersionMapper;

    @BeforeEach
    public void setUp() {
        bpmnVersionMapper = new BpmnVersionMapperImpl();
    }

    @Test
    public void toEntityTest_null(){
        BpmnDTO input = null;
        BpmnVersion resource = bpmnVersionMapper.toEntity(input);
        assertNull(resource);
    }

    @Test
    public void toEntityTest(){
        BpmnDTO input = new BpmnDTO();
        BpmnVersion resource = bpmnVersionMapper.toEntity(input);

        assertNotNull(resource);
        assertEquals(input.getBpmnId(), resource.getBpmnId());
        assertEquals(input.getFunctionType(), resource.getFunctionType());
        assertEquals(input.getModelVersion(), resource.getModelVersion());
        assertEquals(input.getDeployedFileName(), resource.getDeployedFileName());
        assertEquals(input.getDefinitionKey(), resource.getDefinitionKey());
        assertEquals(input.getCreatedAt(), resource.getCreatedAt());
        assertEquals(input.getLastUpdatedAt(), resource.getLastUpdatedAt());
        assertEquals(input.getCreatedBy(), resource.getCreatedBy());
        assertEquals(input.getLastUpdatedBy(), resource.getLastUpdatedBy());
        assertEquals(input.getStatus(), resource.getStatus());
        assertEquals(input.getSha256(), resource.getSha256());
        assertEquals(input.getDefinitionVersionCamunda(), resource.getDefinitionVersionCamunda());
        assertEquals(input.getCamundaDefinitionId(), resource.getCamundaDefinitionId());
        assertEquals(input.getDescription(), resource.getDescription());
        assertEquals(input.getResource(), resource.getResource());
        assertEquals(input.getDeploymentId(), resource.getDeploymentId());

    }


    @Test
    public void resourceFileDTOToResourceFileTest_null(){
        ResourceFileDTO input = null;
        ResourceFile resource = bpmnVersionMapper.resourceFileDTOToResourceFile(input);
        assertNull(resource);
    }

    @Test
    public void resourceFileDTOToResourceFileTest(){
        ResourceFileDTO input = new ResourceFileDTO();
        ResourceFile resource = bpmnVersionMapper.resourceFileDTOToResourceFile(input);

        assertNotNull(resource);
        assertEquals(input.getId(), resource.getId());
        assertEquals(input.getResourceType(), resource.getResourceType());
        assertEquals(input.getStorageKey(), resource.getStorageKey());
        assertEquals(input.getFileName(), resource.getFileName());
        assertEquals(input.getExtension(), resource.getExtension());
        assertEquals(input.getCreatedAt(), resource.getCreatedAt());
        assertEquals(input.getLastUpdatedAt(), resource.getLastUpdatedAt());
        assertEquals(input.getCreatedBy(), resource.getCreatedBy());
        assertEquals(input.getLastUpdatedBy(), resource.getLastUpdatedBy());
    }

    @Test
    public void toDtoCreationTest_null(){
        BpmnVersion input = null;
        BpmnCreationDto resource = bpmnVersionMapper.toDtoCreation(input);
        assertNull(resource);
    }

    @Test
    public void toDtoCreationTest(){
        BpmnVersion input = new BpmnVersion();
        BpmnCreationDto resource = bpmnVersionMapper.toDtoCreation(input);

        assertNotNull(resource);
        assertEquals(input.getFunctionType(), resource.getFunctionType());
    }

    @Test
    public void toProcessDTOTest_null(){
        BpmnDTO input = null;
        BpmnProcessDTO resource = bpmnVersionMapper.toProcessDTO(input);
        assertNull(resource);
    }

    @Test
    public void toProcessDTOTest(){
        BpmnDTO input = new BpmnDTO();
        BpmnProcessDTO resource = bpmnVersionMapper.toProcessDTO(input);

        assertNotNull(resource);
        assertEquals(input.getCamundaDefinitionId(), resource.getCamundaDefinitionId());
    }


}
