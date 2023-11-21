package it.gov.pagopa.atmlayer.service.model.resource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
  void createResourceOK() throws NoSuchAlgorithmException, IOException {
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
}
