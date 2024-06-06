package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.WorkflowResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.mapper.WorkflowResourceMapper;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
class WorkflowResourceResourceTest {

    @InjectMock
    WorkflowResourceService workflowResourceService;

    @InjectMock
    WorkflowResourceMapper workflowResourceMapper;

    @Test
    void testCreate() throws NoSuchAlgorithmException, IOException {
        WorkflowResource workflowResource = new WorkflowResource();
        WorkflowResourceDTO workflowResourceDTO = new WorkflowResourceDTO();

        when(workflowResourceMapper.toEntityCreation(any(WorkflowResourceCreationDto.class))).thenReturn(
                workflowResource);
        when(workflowResourceService.createWorkflowResource(any(WorkflowResource.class), any(File.class),
                any(String.class)))
                .thenReturn(Uni.createFrom().item(workflowResource));
        when(workflowResourceMapper.toDTO(any(WorkflowResource.class))).thenReturn(workflowResourceDTO);

        WorkflowResourceDTO result = given()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", new File("src/test/resources/diagram_1.dmn"))
                .formParam("filename", "diagram_1")
                .formParam("resourceType", "DMN")
                .when().post("/api/v1/model/workflow-resource")
                .then()
                .statusCode(200)
                .extract().as(WorkflowResourceDTO.class);

        assertEquals(workflowResourceDTO, result);
    }

    @Test
    void testUpdate() throws NoSuchAlgorithmException, IOException {
        WorkflowResource workflowResource = new WorkflowResource();
        WorkflowResourceDTO workflowResourceDTO = new WorkflowResourceDTO();
        UUID uuid = UUID.randomUUID();

        when(workflowResourceService.update(any(UUID.class), any(File.class), any(Boolean.class)))
                .thenReturn(Uni.createFrom().item(workflowResource));
        when(workflowResourceMapper.toDTO(any(WorkflowResource.class))).thenReturn(workflowResourceDTO);

        WorkflowResourceDTO result = given()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", new File("src/test/resources/diagram_1.dmn"))
                .pathParam("uuid", uuid.toString())
                .when().put("/api/v1/model/workflow-resource/update/{uuid}")
                .then()
                .statusCode(200)
                .extract().as(WorkflowResourceDTO.class);

        assertEquals(workflowResourceDTO, result);
    }

    @Test
    void testRollback() {
        UUID uuid = UUID.randomUUID();
        WorkflowResource rolledBackWorkflowResource = new WorkflowResource();
        WorkflowResourceDTO expectedDto = new WorkflowResourceDTO();
        expectedDto.setWorkflowResourceId(uuid);

        when(workflowResourceService.rollback(any(UUID.class))).thenReturn(Uni.createFrom().item(rolledBackWorkflowResource));
        when(workflowResourceMapper.toDTO(any(WorkflowResource.class))).thenReturn(expectedDto);

        WorkflowResourceDTO result = given()
                .contentType(ContentType.JSON)
                .pathParam("uuid", uuid)
                .when().put("/api/v1/model/workflow-resource/rollback/{uuid}")
                .then()
                .statusCode(200)
                .extract().as(WorkflowResourceDTO.class);

        verify(workflowResourceService, times(1)).rollback(uuid);
        verify(workflowResourceMapper, times(1)).toDTO(rolledBackWorkflowResource);

        assertEquals(uuid, result.getWorkflowResourceId());
    }

    @Test
    void testDelete() {
        UUID uuid = UUID.randomUUID();

        when(workflowResourceService.delete(any(UUID.class))).thenReturn(Uni.createFrom().item(true));

        given()
                .pathParam("uuid", uuid)
                .when().delete("/api/v1/model/workflow-resource/{uuid}")
                .then()
                .statusCode(204);

        verify(workflowResourceService, times(1)).delete(uuid);
    }

    @Test
    void testDeploy() {
        UUID uuid = UUID.randomUUID();
        WorkflowResource workflowResource = new WorkflowResource();
        WorkflowResourceDTO expectedDto = new WorkflowResourceDTO();
        expectedDto.setWorkflowResourceId(uuid);

        when(workflowResourceService.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(Optional.of(workflowResource)));
        when(workflowResourceMapper.toDTO(any(WorkflowResource.class))).thenReturn(expectedDto);
        when(workflowResourceService.deploy(any(UUID.class), eq(Optional.of(workflowResource)))).thenReturn(Uni.createFrom().item(workflowResource));

        WorkflowResourceDTO result = given()
                .contentType(ContentType.JSON)
                .pathParam("uuid", uuid)
                .when().post("/api/v1/model/workflow-resource/deploy/{uuid}")
                .then()
                .statusCode(200)
                .extract().as(WorkflowResourceDTO.class);

        verify(workflowResourceService, times(1)).findById(uuid);
        verify(workflowResourceService, times(1)).deploy(uuid, Optional.of(workflowResource));
        verify(workflowResourceMapper, times(1)).toDTO(workflowResource);
        assertEquals(uuid, result.getWorkflowResourceId());
    }

    @Test
    void testGetAll() {
        List<WorkflowResource> workflowResources = new ArrayList<>();
        WorkflowResource workflowResource = new WorkflowResource();
        workflowResources.add(workflowResource);
        List<WorkflowResourceDTO> dtoList = new ArrayList<>();
        WorkflowResourceDTO workflowResourceDTO = new WorkflowResourceDTO();
        dtoList.add(workflowResourceDTO);

        when(workflowResourceService.getAll()).thenReturn(Uni.createFrom().item(workflowResources));
        when(workflowResourceMapper.toDTOList(any(ArrayList.class))).thenReturn(dtoList);

        ArrayList result = given()
                .when().get("/api/v1/model/workflow-resource")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ArrayList.class);
        assertEquals(1, result.size());
        verify(workflowResourceService, times(1)).getAll();
        verify(workflowResourceMapper, times(1)).toDTOList(workflowResources);
    }

    @Test
    void testGetAllEmptyList() {
        List<WorkflowResource> emptyList = new ArrayList<>();

        when(workflowResourceService.getAll()).thenReturn(Uni.createFrom().item(emptyList));

        given()
                .when().get("/api/v1/model/workflow-resource")
                .then()
                .statusCode(200);

        verify(workflowResourceService, times(1)).getAll();
    }

    @Test
    void testById() {
        UUID uuid = UUID.randomUUID();

        WorkflowResource workflowResource = new WorkflowResource();

        when(workflowResourceService.findById(any(UUID.class))).thenReturn(
                Uni.createFrom().item(Optional.of(workflowResource)));
        WorkflowResourceDTO dto = new WorkflowResourceDTO();
        when(workflowResourceMapper.toDTO(any(WorkflowResource.class))).thenReturn(dto);

        given()
                .pathParam("uuid", uuid)
                .when()
                .get("/api/v1/model/workflow-resource/{uuid}")
                .then()
                .statusCode(200);

        verify(workflowResourceService, times(1)).findById(any(UUID.class));
        verify(workflowResourceMapper, times(1)).toDTO(workflowResource);
    }

    @Test
    void testByIdNotFound() {
        UUID uuid = UUID.randomUUID();

        when(workflowResourceService.findById(any(UUID.class))).thenReturn(
                Uni.createFrom().item(Optional.empty()));

        given()
                .pathParam("uuid", uuid)
                .when()
                .get("/api/v1/model/workflow-resource/{uuid}")
                .then()
                .statusCode(404);

        verify(workflowResourceService, times(1)).findById(any(UUID.class));
        verify(workflowResourceMapper, times(0)).toDTO(any());
    }

}
