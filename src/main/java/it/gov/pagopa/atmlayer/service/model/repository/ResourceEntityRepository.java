package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ResourceEntityRepository implements PanacheRepositoryBase<ResourceEntity, UUID> {
    public Uni<ResourceEntity> findBySHA256(String sha256) {
        return find("sha256", sha256).firstResult();
    }

    public Uni<PageInfo<ResourceEntity>> findByFilters(Map<String, Object> params, int pageIndex, int pageSize) {
        String queryFilters = params.keySet().stream().map(key -> switch (key) {
            case "resourceId" -> ("r." + key + " = :" + key);
            case "storageKey", "extension" -> ("lower(r.resourceFile." + key +") LIKE lower(concat(concat(:" + key + "), '%'))");
            default -> ("lower(r." + key + ") LIKE lower(concat(concat(:" + key + "), '%'))");
        }).collect(Collectors.joining(" and "));
        PanacheQuery<ResourceEntity> queryResult = find(("select r from ResourceEntity r").concat(queryFilters.isBlank() ? "" : " where " + queryFilters).concat(" order by r.lastUpdatedAt DESC"), params).page(Page.of(pageIndex, pageSize));
        return queryResult.count()
                .onItem().transformToUni(count -> {
                    int totalCount = count.intValue();
                    int totalPages = (int) Math.ceil((double) totalCount / pageSize);
                    return queryResult.list()
                            .onItem()
                            .transform(list -> new PageInfo<ResourceEntity>(pageIndex, pageSize, totalCount, totalPages, list));
                });
    }
}
