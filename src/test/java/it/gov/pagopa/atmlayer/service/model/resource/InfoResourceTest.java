package it.gov.pagopa.atmlayer.service.model.resource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.model.InfoResponse;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

@QuarkusTest
class InfoResourceTest {

    @Inject
    private InfoResource sut;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void infoSuccess() {
        String responseString =
                given()
                        .when().get("/api/v1/model/info")
                        .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        InfoResponse response = objectMapper.readValue(responseString, InfoResponse.class);
        assertNotNull(response);
        assertNotNull(response.getName());
        assertNotNull(response.getEnvironment());
        assertNotNull(response.getDescription());
        assertNotNull(response.getVersion());
    }

}
