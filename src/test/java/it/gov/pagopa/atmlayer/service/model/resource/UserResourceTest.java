package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
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
    UserRepository userRepository;
    @InjectMock
    UserService userService;

    @Test
    void testInsert() {
        User user = new User();
        UserWithProfilesDTO userDTO = new UserWithProfilesDTO();
        UserInsertionDTO userInsertionDTO = new UserInsertionDTO();
        userInsertionDTO.setUserId("prova@test.com");
        userInsertionDTO.setName("prova");
        userInsertionDTO.setSurname("test");

        when(userMapper.toEntityInsertion(userInsertionDTO)).thenReturn(user);
        when(userService.insertUser(userInsertionDTO)).thenReturn(Uni.createFrom().item(user));
        when(userMapper.toProfilesDTO(user)).thenReturn(userDTO);

        UserWithProfilesDTO result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userInsertionDTO)
                .when().post("api/v1/model/users/insert")
                .then()
                .statusCode(200)
                .extract().as(UserWithProfilesDTO.class);

        assertEquals(userDTO, result);
    }

    @Test
    void testFirstAccess() {
        String userId = "testUserId";
        User user = new User();
        UserWithProfilesDTO userWithProfilesDTO = new UserWithProfilesDTO();

        when(userService.checkFirstAccess(userId)).thenReturn(Uni.createFrom().voidItem());
        when(userService.findUser(userId)).thenReturn(Uni.createFrom().item(user));
        when(userMapper.toProfilesDTO(user)).thenReturn(userWithProfilesDTO);

        UserWithProfilesDTO result = given()
                .pathParam("userId", userId)
                .when()
                .post("/api/v1/model/users/first-access/{userId}")
                .then()
                .statusCode(200)
                .extract()
                .as(UserWithProfilesDTO.class);

        assertEquals(userWithProfilesDTO, result);
        verify(userService, times(1)).checkFirstAccess(userId);
        verify(userService, times(1)).findUser(userId);
        verify(userMapper, times(1)).toProfilesDTO(user);
    }


    @Test
    void testUpdate() {
        User user = new User();
        UserWithProfilesDTO userWithProfilesDTO = new UserWithProfilesDTO();
        UserInsertionDTO userInsertionDTO = new UserInsertionDTO();
        userInsertionDTO.setUserId("Paolo@Rossi.com");
        userInsertionDTO.setName("Paolo");
        userInsertionDTO.setSurname("Rossi");

        when(userService.updateUser(any(UserInsertionDTO.class))).thenReturn(Uni.createFrom().item(user));
        when(userMapper.toProfilesDTO(user)).thenReturn(userWithProfilesDTO);

        UserWithProfilesDTO result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userInsertionDTO)
                .when()
                .put("api/v1/model/users/update")
                .then()
                .statusCode(200)
                .extract()
                .as(UserWithProfilesDTO.class);

        assertEquals(userWithProfilesDTO, result);
    }

    @Test
    void testInsertWithProfiles() {
        User user = new User();
        UserProfiles userProfiles = new UserProfiles();
        userProfiles.setUserProfilesPK(new UserProfilesPK("prova@test.com", 1));
        userProfiles.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userProfiles.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

        List<UserProfiles> userProfilesList = new ArrayList<>();
        userProfilesList.add(userProfiles);

        List<Integer> profileIds = new ArrayList<>();
        profileIds.add(1);

        user.setUserId("prova@test.com");
        user.setName("prova");
        user.setSurname("test");
        user.setUserProfiles(userProfilesList);

        UserInsertionWithProfilesDTO userInsertionWithProfilesDTO = new UserInsertionWithProfilesDTO();
        userInsertionWithProfilesDTO.setUserId("prova@test.com");
        userInsertionWithProfilesDTO.setName("prova");
        userInsertionWithProfilesDTO.setSurname("test");
        userInsertionWithProfilesDTO.setProfileIds(profileIds);

        UserWithProfilesDTO userWithProfilesDTO = new UserWithProfilesDTO();

        when(userService.insertUserWithProfiles(userInsertionWithProfilesDTO)).thenReturn(Uni.createFrom().item(user));
        when(userRepository.findByIdCustom(userInsertionWithProfilesDTO.getUserId())).thenReturn(Uni.createFrom().item(user));
        when(userMapper.toProfilesDTO(user)).thenReturn(userWithProfilesDTO);

        UserWithProfilesDTO result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userInsertionWithProfilesDTO)
                .when().post("api/v1/model/users/insert-with-profiles")
                .then()
                .statusCode(200)
                .extract().as(UserWithProfilesDTO.class);

        assertEquals(userWithProfilesDTO, result);
    }

    @Test
    void testDelete() {
        String userId = "testUserId";

        when(userService.deleteUser(userId)).thenReturn(Uni.createFrom().item(true));

        given()
                .pathParam("userId", userId)
                .when().delete("/api/v1/model/users/delete/userId/{userId}")
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
                .when().get("/api/v1/model/users")
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
    void testGetAllEmpty() {
        List<User> users = new ArrayList<>();
        List<UserWithProfilesDTO> dtoList = new ArrayList<>();

        when(userService.getAllUsers()).thenReturn(Uni.createFrom().item(users));
        when(userMapper.toDTOList(any(List.class))).thenReturn(dtoList);

        ArrayList result = given()
                .when().get("/api/v1/model/users")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ArrayList.class);

        assertEquals(0, result.size());
        verify(userService, times(1)).getAllUsers();
        verify(userMapper, times(1)).toDTOList(users);
    }

    @Test
    void testGetByIdWithProfiles() {
        String userId = "testUserId";
        User user = new User();
        UserWithProfilesDTO userWithProfilesDTO = new UserWithProfilesDTO();

        when(userService.getById(any(String.class))).thenReturn(Uni.createFrom().item(user));
        when(userMapper.toProfilesDTO(any(User.class))).thenReturn(userWithProfilesDTO);

        UserWithProfilesDTO result = given()
                .pathParam("userId", userId)
                .when()
                .get("/api/v1/model/users/{userId}")
                .then()
                .statusCode(200)
                .extract()
                .as(UserWithProfilesDTO.class);

        assertEquals(userWithProfilesDTO, result);
        verify(userService, times(1)).getById(any(String.class));
        verify(userMapper, times(1)).toProfilesDTO(user);
    }

}