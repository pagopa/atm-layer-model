package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UserProfilesRepository implements PanacheRepositoryBase<UserProfiles, UserProfilesPK> {

    /*public Uni<List<UserProfiles>> findByUserIdAndProfileId(String) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("profileId", profileId);
        return list("select u from UserProfiles u where u.userProfilesPK.userId = :userId and u.profileId = :profileId", params);
    }*/

}
