package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
class UserMapperTest {

    @Inject
    UserMapper mapper;

    @Test
    void toEntityInsertionTest() {
        UserInsertionDTO userInsertionDTO = new UserInsertionDTO();
        userInsertionDTO.setUserId("prova@test.com");
        userInsertionDTO.setName("prova");
        userInsertionDTO.setSurname("test");

        mapper.toEntityInsertion(userInsertionDTO);
    }

    @Test
    void toProfilesDTOTestNotNullWithListNotNull() {
        User user = new User();
        List<UserProfiles> list = new ArrayList<>();

        Instant fixedInstant = Instant.parse("2024-10-01T00:00:00Z");
        user.setUserId("prova@test.com");
        user.setName("prova");
        user.setSurname("test");
        user.setUserProfiles(list);
        user.setCreatedAt(Timestamp.from(fixedInstant));
        user.setLastUpdatedAt(Timestamp.from(fixedInstant));

        mapper.toProfilesDTO(user);
    }

    @Test
    void toProfilesDTOTestNullWithListNull() {
        User user = new User();
        mapper.toProfilesDTO(null);

        Instant fixedInstant = Instant.parse("2024-10-01T00:00:00Z");
        user.setUserId("prova@test.com");
        user.setName("prova");
        user.setSurname("test");
        user.setUserProfiles(null);
        user.setCreatedAt(Timestamp.from(fixedInstant));
        user.setLastUpdatedAt(Timestamp.from(fixedInstant));

        mapper.toProfilesDTO(user);
    }
}
