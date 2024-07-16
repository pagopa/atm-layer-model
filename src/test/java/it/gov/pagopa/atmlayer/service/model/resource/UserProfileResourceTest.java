package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileAllDto;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileDto;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;
import it.gov.pagopa.atmlayer.service.model.mapper.UserProfileMapper;
import it.gov.pagopa.atmlayer.service.model.service.UserProfileService;
import it.gov.pagopa.atmlayer.service.model.validators.UserProfileValidator;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserProfileResourceTest {

    @InjectMock
     UserProfileService userProfileService;
    @InjectMock
    UserProfileMapper userProfileMapper;
    @InjectMock
    UserProfileValidator userProfileValidator;

    @Test
    void testFindByUserId() {
        String userId = "email@domain.com";

        UserProfile userProfile = new UserProfile();

        when(userProfileService.findByUserId(any(String.class))).thenReturn(
                Uni.createFrom().item(userProfile));
        UserProfileDto userDto = new UserProfileDto();
        when(userProfileMapper.toUserProfileDtoWithProfileMapping(any(UserProfile.class))).thenReturn(userDto);

        given()
                .queryParam("userId", userId)
                .when()
                .get("/api/v1/model/users/search")
                .then()
                .statusCode(200);

        verify(userProfileService, times(1)).findByUserId(any(String.class));
        verify(userProfileMapper, times(1)).toUserProfileDtoWithProfileMapping(userProfile);
    }

//    @Test
//    void testGetAllUsersEmptyList() {
//        List<UserProfile> list = new ArrayList<>();
//
//        when(userProfileService.getUsers()).thenReturn(Uni.createFrom().item(list));
//
//        given()
//                .when().get("/api/v1/model/users")
//                .then()
//                .statusCode(200);
//
//        verify(userProfileService, times(1)).getUsers();
//    }

//   @Test
//   void testGetAllUsersList() {
//        List<UserProfile> userProfileList = new ArrayList<>();
//        UserProfile userProfile = new UserProfile();
//        userProfileList.add(userProfile);
//
//        List<UserProfileAllDto> userProfileDtoList = new ArrayList<>();
//        UserProfileAllDto userProfileDto = new UserProfileAllDto();
//        userProfileDtoList.add(userProfileDto);
//        when(userProfileService.getUsers()).thenReturn(Uni.createFrom().item(userProfileList));
//        when(userProfileMapper.toDtoAllList(any(ArrayList.class))).thenReturn(userProfileDtoList);
//        ArrayList result = given()
//                .when().get("/api/v1/model/users")
//                .then()
//                .statusCode(200)
//                .extract()
//                .body()
//                .as(ArrayList.class);
//        assertEquals(1, result.size());
//        verify(userProfileService, times(1)).getUsers();
//        verify(userProfileMapper, times(1)).toDtoAllList(userProfileList);
//    }

    @Test
    void testCreateUser() {
        UserProfile userProfile = new UserProfile();
        UserProfileAllDto userProfileDto = new UserProfileAllDto();
        UserProfileCreationDto userProfileCreationDto = new UserProfileCreationDto();
        userProfileCreationDto.setUserId("email@domain.com");
        userProfileCreationDto.setProfile(1);
        userProfileCreationDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userProfileCreationDto.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(userProfileMapper.toUserProfile(any(UserProfileCreationDto.class)))
                .thenReturn(userProfile);
        when(userProfileService.createUser(any(UserProfileCreationDto.class)))
                .thenReturn(Uni.createFrom().item(userProfile));
        when(userProfileMapper.toUserProfileAllDto(any(UserProfile.class)))
                .thenReturn(userProfileDto);
        when(userProfileValidator.validateExistenceProfileType(1))
                .thenReturn(Uni.createFrom().voidItem());

        UserProfileAllDto result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userProfileCreationDto)
                .when().post("/api/v1/model/users")
                .then()
                .statusCode(200)
                .extract().as(UserProfileAllDto.class);

        assertEquals(userProfileDto, result);

    }

    @Test
    void testUpdateUser() {
        UserProfile userProfile = new UserProfile();
        UserProfileAllDto userProfileDto = new UserProfileAllDto();
        UserProfileCreationDto userProfileCreationDto = new UserProfileCreationDto();
        userProfileCreationDto.setUserId("email@domain.com");
        userProfileCreationDto.setProfile(2);
       userProfileCreationDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userProfileCreationDto.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(userProfileService.updateUser(any(UserProfileCreationDto.class)))
                .thenReturn(Uni.createFrom().item(userProfile));
        when(userProfileMapper.toUserProfileAllDto(any(UserProfile.class))).thenReturn(userProfileDto);
        when(userProfileValidator.validateExistenceProfileType(1))
                .thenReturn(Uni.createFrom().voidItem());

        UserProfileAllDto result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userProfileCreationDto)
                .when().put("/api/v1/model/users")
                .then()
                .statusCode(200)
                .extract().as(UserProfileAllDto.class);

        assertEquals(userProfileDto, result);
    }

    @Test
    void testDeleteUser() {
        String userId = "email@domain.com";

        when(userProfileService.deleteUser(any(String.class)))
                .thenReturn(Uni.createFrom().voidItem());

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("userId", userId)
                .when().delete("/api/v1/model/users/search")
                .then()
                .statusCode(204);
    }
}