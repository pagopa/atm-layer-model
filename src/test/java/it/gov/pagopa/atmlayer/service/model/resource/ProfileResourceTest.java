package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.mapper.ProfileMapper;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import it.gov.pagopa.atmlayer.service.model.service.ProfileService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProfileResourceTest {

    @InjectMock
    ProfileService profileService;

    @InjectMock
    ProfileMapper profileMapper;

    @Test
    void testCreateProfile() {
        ProfileCreationDto profileCreationDto = new ProfileCreationDto();
        profileCreationDto.setProfileId(1);
        profileCreationDto.setDescription("1");

        Profile profile = new Profile();
        profile.setProfileId(1);
        profile.setDescription("1");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1);
        profileDTO.setDescription("1");

        when(profileService.createProfile(any(ProfileCreationDto.class))).thenReturn(Uni.createFrom().item(profile));
        when(profileMapper.toDto(any(Profile.class))).thenReturn(profileDTO);

        ProfileDTO result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileCreationDto)
                .when().post("/api/v1/model/profile")
                .then()
                .statusCode(200)
                .extract().as(ProfileDTO.class);

        assertEquals(profileCreationDto.getProfileId(), result.getProfileId());
        assertEquals(profileCreationDto.getDescription(), result.getDescription());
    }

    @Test
    void TestGetProfileById() {
        Profile profile = new Profile();
        profile.setProfileId(1);
        profile.setDescription("1");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1);
        profileDTO.setDescription("1");

        when(profileService.retrieveProfile(1)).thenReturn(Uni.createFrom().item(profile));
        when(profileMapper.toDto(any(Profile.class))).thenReturn(profileDTO);

        ProfileDTO result = given()
                .pathParam("profileId", 1)
                .when().get("/api/v1/model/profile/{profileId}")
                .then()
                .statusCode(200)
                .extract().as(ProfileDTO.class);

        assertEquals(profileDTO.getProfileId(), result.getProfileId());
        assertEquals(profileDTO.getDescription(), result.getDescription());
    }

    @Test
    void testUpdateProfile() {
        ProfileCreationDto profileCreationDto = new ProfileCreationDto();
        profileCreationDto.setProfileId(1);
        profileCreationDto.setDescription("2");

        Profile profile = new Profile();
        profile.setProfileId(1);
        profile.setDescription("2");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1);
        profileDTO.setDescription("2");

        when(profileService.updateProfile(any(ProfileCreationDto.class))).thenReturn(Uni.createFrom().item(profile));
        when(profileMapper.toDto(any(Profile.class))).thenReturn(profileDTO);

        ProfileDTO result = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileCreationDto)
                .when().put("/api/v1/model/profile")
                .then()
                .statusCode(200)
                .extract().as(ProfileDTO.class);

        assertEquals(profileCreationDto.getProfileId(), result.getProfileId());
        assertEquals(profileCreationDto.getDescription(), result.getDescription());
    }

    @Test
    void TestDeleteProfileById() {
        Profile profile = new Profile();
        profile.setProfileId(1);
        profile.setDescription("1");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1);
        profileDTO.setDescription("1");

        when(profileService.deleteProfile(1)).thenReturn(Uni.createFrom().voidItem());

        given()
                .pathParam("profileId", 1)
                .when().delete("/api/v1/model/profile/{profileId}")
                .then()
                .statusCode(204);
    }

    @Test
    void TestGetAllProfiles() {
        Profile profile = new Profile();
        profile.setProfileId(1);
        profile.setDescription("2");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(1);
        profileDTO.setDescription("2");

        List<Profile> profilesList = new ArrayList<>();
        profilesList.add(profile);

        List<ProfileDTO> profilesDtoList = new ArrayList<>();
        profilesDtoList.add(profileDTO);


        when(profileService.getAll()).thenReturn(Uni.createFrom().item(profilesList));
        when(profileMapper.toDTOList(any(ArrayList.class))).thenReturn(profilesDtoList);

        ArrayList result =
                given().
                        when().get("/api/v1/model/profile")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(ArrayList.class);

        assertEquals(1, result.size());
        verify(profileService, times(1)).getAll();
        verify(profileMapper, times(1)).toDTOList(profilesList);
    }

    @Test
    void TestGetAllProfilesListEmpty() {
        List<Profile> profilesList = new ArrayList<>();
        List<ProfileDTO> profilesDtoList = new ArrayList<>();

        when(profileService.getAll()).thenReturn(Uni.createFrom().item(profilesList));
        when(profileMapper.toDTOList(any(ArrayList.class))).thenReturn(profilesDtoList);

        ArrayList result =
                given().
                        when().get("/api/v1/model/profile")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(ArrayList.class);

        assertEquals(0, result.size());
        verify(profileService, times(1)).getAll();
        verify(profileMapper, times(1)).toDTOList(profilesList);
    }
}
