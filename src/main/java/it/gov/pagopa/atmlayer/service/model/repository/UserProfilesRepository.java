package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserProfilesRepository implements PanacheRepositoryBase<UserProfiles, UserProfilesPK> {

    public Uni<Long> deleteUserProfiles(List<UserProfilesPK> pKList) {
        return delete("delete from UserProfiles b where b.userProfilesPK in :pKList",
                Parameters.with("pKList", pKList));
    }

    public Uni<List<UserProfiles>> findByUserId (String userId) {
        return find("select a from UserProfiles a where a.userProfilesPK.userId = :userId",
                Parameters.with("userId", userId)).list();
    }

    public Uni<List<UserProfiles>> findUserProfilesWithSpecificProfile() {
        return find("select a from UserProfiles a where a.userProfilesPK.profileId = 5").list();
    }

}
