package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class UserProfileRepositoryTest {

    @Test
    void testFindUserId() {

        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId("email@domain.com");
        userProfile.setProfile(1);
        List<UserProfile> mockResult = Collections.singletonList(userProfile);

        Mockito.when(repository.findUserId("email@domain.com"))
                .thenReturn(Uni.createFrom().item(mockResult.get(0)));

        UserProfile result = repository.findUserId("email@domain.com").await().indefinitely();

        assertThat(result.getUserId(), is(equalTo("email@domain.com")));
    }
}