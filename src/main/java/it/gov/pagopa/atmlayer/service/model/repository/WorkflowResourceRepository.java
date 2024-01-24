package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WorkflowResourceRepository implements PanacheRepositoryBase<WorkflowResource, UUID> {

    public Uni<WorkflowResource> findBySHA256(String sha256) {
        return find("sha256", sha256).firstResult();
    }

    public Uni<WorkflowResource> findByDefinitionKey(String definitionKey) {
        return find("definitionKey", definitionKey).firstResult();
    }

    @Transactional
    public Uni<List<WorkflowResource>> findByStatus(StatusEnum status, int page, int size) {
        return find("status = ?1", status/*, Sort.ascending("id")*/)
                .page(page, size)
                .list();
    }
}
