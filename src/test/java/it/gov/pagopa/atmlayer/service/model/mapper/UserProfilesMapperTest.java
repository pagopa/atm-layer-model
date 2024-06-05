package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class UserProfilesMapperTest {
    @Inject
    UserProfilesMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserProfilesMapper.class);
    }

    @Test
    void testToEntityInsertion() {
        UserProfilesInsertionDTO userProfilesInsertionDTO = new UserProfilesInsertionDTO();
        List<Integer> idsList = new ArrayList<>();
        idsList.add(1);
        userProfilesInsertionDTO.setProfileIds(idsList);
        mapper.toEntityInsertion(userProfilesInsertionDTO);
    }

    @Test
    void testToDtoList() {
        UserProfilesPK userProfilesPK1 = new UserProfilesPK("1", 1);
        Timestamp createdAt1 = new Timestamp(System.currentTimeMillis());
        Timestamp lastUpdatedAt1 = new Timestamp(System.currentTimeMillis());

        UserProfiles userProfiles1 = new UserProfiles();
        userProfiles1.setUserProfilesPK(userProfilesPK1);
        userProfiles1.setCreatedAt(createdAt1);
        userProfiles1.setLastUpdatedAt(lastUpdatedAt1);

        UserProfilesPK userProfilesPK2 = new UserProfilesPK("2", 2);
        Timestamp createdAt2 = new Timestamp(System.currentTimeMillis());
        Timestamp lastUpdatedAt2 = new Timestamp(System.currentTimeMillis());

        UserProfiles userProfiles2 = new UserProfiles();
        userProfiles2.setUserProfilesPK(userProfilesPK2);
        userProfiles2.setCreatedAt(createdAt2);
        userProfiles2.setLastUpdatedAt(lastUpdatedAt2);

        List<UserProfiles> userProfilesList = new ArrayList<>();
        userProfilesList.add(userProfiles1);
        userProfilesList.add(userProfiles2);

        UserProfilesDTO expectedUserProfilesDTO1 = UserProfilesDTO.builder()
                .userId("1")
                .profileId(1)
                .createdAt(createdAt1)
                .lastUpdatedAt(lastUpdatedAt1)
                .build();

        UserProfilesDTO expectedUserProfilesDTO2 = UserProfilesDTO.builder()
                .userId("2")
                .profileId(2)
                .createdAt(createdAt2)
                .lastUpdatedAt(lastUpdatedAt2)
                .build();

        List<UserProfilesDTO> expectedUserProfilesDTOList = new ArrayList<>();
        expectedUserProfilesDTOList.add(expectedUserProfilesDTO1);
        expectedUserProfilesDTOList.add(expectedUserProfilesDTO2);

        List<UserProfilesDTO> actualUserProfilesDTOList = mapper.toDtoList(userProfilesList);

        assertEquals(expectedUserProfilesDTOList, actualUserProfilesDTOList);
    }

    @Test
    void testToDTO() {

        UserProfilesPK userProfilesPK = new UserProfilesPK("1", 1);
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Timestamp lastUpdatedAt = new Timestamp(System.currentTimeMillis());

        UserProfiles userProfiles = new UserProfiles();
        userProfiles.setUserProfilesPK(userProfilesPK);
        userProfiles.setCreatedAt(createdAt);
        userProfiles.setLastUpdatedAt(lastUpdatedAt);

        UserProfilesDTO expectedUserProfilesDTO = UserProfilesDTO.builder()
                .userId("1")
                .profileId(1)
                .createdAt(createdAt)
                .lastUpdatedAt(lastUpdatedAt)
                .build();

        UserProfilesDTO actualUserProfilesDTO = mapper.toDTO(userProfiles);

        assertEquals(expectedUserProfilesDTO, actualUserProfilesDTO);
    }

    private String userProfilesUserProfilesPKUserId(UserProfiles userProfiles) {
        if (userProfiles == null) {
            return null;
        }
        UserProfilesPK userProfilesPK = userProfiles.getUserProfilesPK();
        if (userProfilesPK == null) {
            return null;
        }
        return userProfilesPK.getUserId();
    }

    private int userProfilesUserProfilesPKProfileId(UserProfiles userProfiles) {
        if (userProfiles == null) {
            return 0;
        }
        UserProfilesPK userProfilesPK = userProfiles.getUserProfilesPK();
        if (userProfilesPK == null) {
            return 0;
        }
        return userProfilesPK.getProfileId();
    }
}
