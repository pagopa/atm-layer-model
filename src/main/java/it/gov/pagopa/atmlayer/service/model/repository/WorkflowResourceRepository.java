package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class WorkflowResourceRepository implements PanacheRepositoryBase<WorkflowResource, UUID> {

    public Uni<WorkflowResource> findBySHA256(String sha256) {
        return find("sha256", sha256).firstResult();
    }

    public Uni<WorkflowResource> findByDefinitionKey(String definitionKey) {
        return find("definitionKey", definitionKey).firstResult();
    }

    public Uni<List<WorkflowResource>> findByStatus(StatusEnum status, int page, int size) {
        return find("status = ?1", status/*, Sort.ascending("id")*/)
                .page(page, size)
                .list();
    }

    public Uni<List<WorkflowResource>> findByFilters(Map<String, Object> params, int pageIndex, int pageSize) {
        String queryFilters = params.keySet().stream()
                .map(key -> "b." + key + " LIKE concat(concat('%', :" + key + "), '%')")
                .collect(Collectors.joining(" and "));
        return find(("select b from WorkflowResource b").concat(queryFilters.isBlank()?"":" where " + queryFilters), params).page(Page.of(pageIndex, pageSize)).list();
    }
}
