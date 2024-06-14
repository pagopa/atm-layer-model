package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, String> {
    public Uni<User> findByIdCustom(String userId) {
        return find("select u from User u left join fetch u.userProfiles where u.userId = :userId",
                Parameters.with("userId", userId)).firstResult();
    }

    public PanacheQuery<User> findAllCustom() {
        return find("select u from User u left join fetch u.userProfiles");
    }
}
