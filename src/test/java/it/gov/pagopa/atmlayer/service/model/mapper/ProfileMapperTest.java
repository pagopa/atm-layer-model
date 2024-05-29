package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class ProfileMapperTest {
    @Inject
    ProfileMapper mapper;
    @Test
    void toDtoListTest () {
        List<Profile> profileList = new ArrayList<>();
        mapper.toDTOList(profileList);
    }
}
