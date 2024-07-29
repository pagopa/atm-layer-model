package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class UserProfilesRepositoryTest {

    @Test
    void testDeleteUserProfiles() {
        UserProfilesRepository repositoryMock = Mockito.mock(UserProfilesRepository.class);

        UserProfilesPK pk1 = new UserProfilesPK();
        UserProfilesPK pk2 = new UserProfilesPK();

        pk1.setUserId("user1");
        pk2.setUserId("user2");

        List<UserProfilesPK> pkList = List.of(pk1, pk2);

        Mockito.when(repositoryMock.deleteUserProfiles(pkList))
                .thenReturn(Uni.createFrom().item(2L));

        Long res = repositoryMock.deleteUserProfiles(pkList).await().indefinitely();

        assertThat(res, is(equalTo(2L)));
    }

    @Test
    void testFindByUserId() {
        UserProfilesRepository repositoryMock = Mockito.mock(UserProfilesRepository.class);

        String testUserId = "test-user-id";
        UserProfiles userProfile1 = new UserProfiles();
        UserProfiles userProfile2 = new UserProfiles();

        UserProfilesPK pk1 = new UserProfilesPK();
        UserProfilesPK pk2 = new UserProfilesPK();

        pk1.setUserId(testUserId);
        pk2.setUserId(testUserId);

        userProfile1.setUserProfilesPK(pk1);
        userProfile2.setUserProfilesPK(pk2);

        List<UserProfiles> userList = List.of(userProfile1, userProfile2);

        Mockito.when(repositoryMock.findByUserId(testUserId))
                .thenReturn(Uni.createFrom().item(userList));

        List<UserProfiles> res = repositoryMock.findByUserId(testUserId).await().indefinitely();

        assertThat(res.size(), is(equalTo(2)));
        assertThat(res.get(0).getUserProfilesPK().getUserId(), is(equalTo(testUserId)));
        assertThat(res.get(1).getUserProfilesPK().getUserId(), is(equalTo(testUserId)));
    }
}
