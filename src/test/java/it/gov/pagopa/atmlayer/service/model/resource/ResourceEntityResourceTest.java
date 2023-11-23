package it.gov.pagopa.atmlayer.service.model.resource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceEntityMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.ResourceFileMapper;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityService;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ResourceEntityResourceTest {

  @InjectMock
  private ResourceEntityMapper resourceEntityMapper;

  @InjectMock
  private ResourceFileMapper resourceFileMapper;

  @InjectMock
  private ResourceEntityService resourceEntityService;

  @Test
  void testCreateResource() throws NoSuchAlgorithmException, IOException {
    ResourceEntity resourceEntity = new ResourceEntity();
    ResourceDTO resourceDTO = new ResourceDTO();

    when(resourceEntityMapper.toEntityCreation(any(ResourceCreationDto.class))).thenReturn(
        resourceEntity);
    when(resourceEntityService.createResource(any(ResourceEntity.class), any(File.class),
        any(String.class), any(String.class)))
        .thenReturn(Uni.createFrom().item(resourceEntity));
    when(resourceEntityMapper.toDTO(any(ResourceEntity.class))).thenReturn(resourceDTO);

    ResourceDTO result = given()
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .multiPart("file", new File("src/test/resources/TestMalformed.bpmn"))
        .formParam("filename", "name.bpmn")
        .formParam("resourceType", "OTHER")
        .formParam("path", "")
        .when().post("/api/v1/model/resources")
        .then()
        .statusCode(200)
        .extract().as(ResourceDTO.class);

    assertEquals(resourceDTO, result);
  }

  @Test
  void testUpdateResource() throws NoSuchAlgorithmException, IOException {
    ResourceEntity resourceEntity = new ResourceEntity();
    ResourceDTO resourceDTO = new ResourceDTO();
    UUID uuid = UUID.randomUUID();

    when(resourceEntityService.updateResource(any(UUID.class), any(File.class)))
        .thenReturn(Uni.createFrom().item(resourceEntity));
    when(resourceEntityMapper.toDTO(any(ResourceEntity.class))).thenReturn(resourceDTO);

    ResourceDTO result = given()
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .multiPart("file", new File("src/test/resources/TestMalformed.bpmn"))
        .pathParam("uuid", uuid.toString())
        .when().put("/api/v1/model/resources/{uuid}")
        .then()
        .statusCode(200)
        .extract().as(ResourceDTO.class);

    assertEquals(resourceDTO, result);
  }


  @Test
  void testGetAllResources() {
    List<ResourceEntity> resourceEntities = new ArrayList<>();
    ResourceEntity resourceEntity = new ResourceEntity();
    resourceEntities.add(resourceEntity);
    List<ResourceDTO> dtoList = new ArrayList<>();
    ResourceDTO resourceDTO = new ResourceDTO();
    dtoList.add(resourceDTO);
    when(resourceEntityService.getAll()).thenReturn(Uni.createFrom().item(resourceEntities));
    when(resourceEntityMapper.toDTOList(any(ArrayList.class))).thenReturn(dtoList);
    ArrayList result = given()
        .when().get("/api/v1/model/resources")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(ArrayList.class);
    assertEquals(1, result.size());
    verify(resourceEntityService, times(1)).getAll();
    verify(resourceEntityMapper, times(1)).toDTOList(resourceEntities);
  }

  @Test
  void testGetAllResourcesEmptyList() {
    List<ResourceEntity> resourceEntities = new ArrayList<>();
    ResourceEntity resourceEntity = new ResourceEntity();
    resourceEntities.add(resourceEntity);
    List<ResourceDTO> dtoList = new ArrayList<>();
    when(resourceEntityService.getAll()).thenReturn(Uni.createFrom().item(resourceEntities));
    when(resourceEntityMapper.toDTOList(any(ArrayList.class))).thenReturn(dtoList);
    ArrayList result = given()
        .when().get("/api/v1/model/resources")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(ArrayList.class);
    Assertions.assertTrue(result.isEmpty());
    verify(resourceEntityService, times(1)).getAll();
    verify(resourceEntityMapper, times(1)).toDTOList(resourceEntities);
  }

  @Test
  void testGetResourceById() {
    UUID uuid = UUID.randomUUID();
    ResourceEntity resourceEntity = new ResourceEntity();
    when(resourceEntityService.findByUUID(any(UUID.class))).thenReturn(
        Uni.createFrom().item(Optional.of(resourceEntity)));
    ResourceDTO resourceDTO = new ResourceDTO();
    when(resourceEntityMapper.toDTO(any(ResourceEntity.class))).thenReturn(resourceDTO);
    given()
        .pathParam("uuid", uuid)
        .when()
        .get("/api/v1/model/resources/{uuid}")
        .then()
        .statusCode(200);

    verify(resourceEntityService, times(1)).findByUUID(any(UUID.class));
    verify(resourceEntityMapper, times(1)).toDTO(resourceEntity);
  }
}
