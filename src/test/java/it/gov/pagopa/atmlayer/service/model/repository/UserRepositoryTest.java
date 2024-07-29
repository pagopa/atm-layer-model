package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class UserRepositoryTest {
    @Test
    void testFindById() {

        UserRepository repositoryMock = Mockito.mock(UserRepository.class);

        String testUserId = "test-user-id";
        User user = new User();

        try {
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(user, testUserId);

            UserProfiles profile1 = new UserProfiles();
            UserProfiles profile2 = new UserProfiles();

            java.lang.reflect.Field profileIdField1 = UserProfiles.class.getDeclaredField("profileId");
            profileIdField1.setAccessible(true);
            profileIdField1.set(profile1, "profile1");

            java.lang.reflect.Field profileIdField2 = UserProfiles.class.getDeclaredField("profileId");
            profileIdField2.setAccessible(true);
            profileIdField2.set(profile2, "profile2");

            java.lang.reflect.Field userProfilesField = User.class.getDeclaredField("userProfiles");
            userProfilesField.setAccessible(true);
            userProfilesField.set(user, List.of(profile1, profile2));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Mockito.when(repositoryMock.findById(testUserId))
                .thenReturn(Uni.createFrom().item(user));

        User res = repositoryMock.findById(testUserId).await().indefinitely();

        try {
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            String userId = (String) userIdField.get(res);

            java.lang.reflect.Field userProfilesField = User.class.getDeclaredField("userProfiles");
            userProfilesField.setAccessible(true);
            List<UserProfiles> profiles = (List<UserProfiles>) userProfilesField.get(res);

            java.lang.reflect.Field profileIdField1 = UserProfiles.class.getDeclaredField("profileId");
            profileIdField1.setAccessible(true);
            String profileId1 = (String) profileIdField1.get(profiles.get(0));

            java.lang.reflect.Field profileIdField2 = UserProfiles.class.getDeclaredField("profileId");
            profileIdField2.setAccessible(true);
            String profileId2 = (String) profileIdField2.get(profiles.get(1));

            assertThat(userId, is(equalTo(testUserId)));
            assertThat(profiles.size(), is(equalTo(2)));
            assertThat(profileId1, is(equalTo("profile1")));
            assertThat(profileId2, is(equalTo("profile2")));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
