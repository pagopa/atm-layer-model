package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class UserProfilesMapperTest {
    @Inject
    UserProfilesMapper mapper;

    @Test
    void toEntityInsertionTest() {
        UserProfilesInsertionDTO userProfilesInsertionDTO = new UserProfilesInsertionDTO();
        List<Integer> idsList = new ArrayList<>();
        idsList.add(1);
        userProfilesInsertionDTO.setProfileIds(idsList);
        mapper.toEntityInsertion(userProfilesInsertionDTO);
    }
}
