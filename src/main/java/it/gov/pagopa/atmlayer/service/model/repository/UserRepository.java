package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, String> {
    public Uni<User> findByIdCustom(String userId) {
        return find("select u from User u left join fetch u.userProfiles where u.userId = :userId",
                Parameters.with("userId", userId)).firstResult();
    }

    public PanacheQuery<User> findAllCustom() {
        return find("select u from User u left join fetch u.userProfiles");
    }

    public Uni<PageInfo<User>> findByFilters(Map<String, Object> params, int pageIndex, int pageSize) {
        StringBuilder query = new StringBuilder("select distinct u from User u left join fetch u.userProfiles up where 1=1 ");

        params.forEach((key, value) -> query.append("and lower(u.").append(key).append(")").append(" LIKE lower(concat(concat(:" + key + "), '%'))"));

        query.append("order by u.lastUpdatedAt DESC");

        PanacheQuery<User> countQuery = find("select distinct u from User u where 1=1 " +
                        params.keySet().stream().map(key -> "and u." + key + " = :" + key + " ").reduce("", String::concat),
                params);

        PanacheQuery<User> fetchQuery = find(query.toString(), params).page(Page.of(pageIndex, pageSize));

        return countQuery.count()
                .onItem().transformToUni(count -> {
                    int totalCount = count.intValue();
                    int totalPages = (int) Math.ceil((double) totalCount / pageSize);
                    return fetchQuery.list()
                            .onItem().transform(list -> new PageInfo<>(pageIndex, pageSize, totalCount, totalPages, list));
                });
    }

}
