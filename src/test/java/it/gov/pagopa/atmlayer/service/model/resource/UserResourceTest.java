package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserResourceTest {

    @InjectMock
    UserMapper userMapper;

    @InjectMock
    UserService userService;

    /*@Test
    void testInsert() {
        String userId = "testUserId";
        User user = new User();
        UserWithProfilesDTO userDTO = new UserWithProfilesDTO();

        when(userMapper.toEntityInsertion(userId)).thenReturn(user);
        when(userService.insertUser(user)).thenReturn(Uni.createFrom().item(user));
        when(userMapper.toProfilesDTO(user)).thenReturn(userDTO);

        UserWithProfilesDTO result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .pathParam("userId", userId)
                .when().post("api/v1/model/user/insert/userId/{userId}")
                .then()
                .statusCode(200)
                .extract().as(UserWithProfilesDTO.class);

        assertEquals(userDTO, result);
    }*/

    @Test
    void testDelete() {
        String userId = "testUserId";

        when(userService.deleteUser(userId)).thenReturn(Uni.createFrom().item(true));

        given()
                .pathParam("userId", userId)
                .when().delete("/api/v1/model/user/delete/userId/{userId}")
                .then()
                .statusCode(204);

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testGetAll() {
        List<User> users = new ArrayList<>();
        User user = new User();
        users.add(user);
        List<UserWithProfilesDTO> dtoList = new ArrayList<>();
        UserWithProfilesDTO userWithProfilesDTO = new UserWithProfilesDTO();
        dtoList.add(userWithProfilesDTO);

        when(userService.getAllUsers()).thenReturn(Uni.createFrom().item(users));
        when(userMapper.toDTOList(any(List.class))).thenReturn(dtoList);

        ArrayList result = given()
                .when().get("/api/v1/model/user")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ArrayList.class);

        assertEquals(1, result.size());
        verify(userService, times(1)).getAllUsers();
        verify(userMapper, times(1)).toDTOList(users);
    }

    @Test
    void testGetByIdWithProfiles() {
        String userId = "testUserId";
        User user = new User();
        UserWithProfilesDTO userWithProfilesDTO = new UserWithProfilesDTO();

        when(userService.findById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userMapper.toProfilesDTO(any(User.class))).thenReturn(userWithProfilesDTO);

        UserWithProfilesDTO result = given()
                .pathParam("userId", userId)
                .when()
                .get("/api/v1/model/user/{userId}")
                .then()
                .statusCode(200)
                .extract()
                .as(UserWithProfilesDTO.class);

        assertEquals(userWithProfilesDTO, result);
        verify(userService, times(1)).findById(any(String.class));
        verify(userMapper, times(1)).toProfilesDTO(user);
    }

}
