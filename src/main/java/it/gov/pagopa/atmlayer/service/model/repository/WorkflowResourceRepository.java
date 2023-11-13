package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class WorkflowResourceRepository implements PanacheRepositoryBase<WorkflowResource, UUID> {

    public Uni<WorkflowResource> findBySHA256(String sha256) {
        return find("sha256", sha256).firstResult();
    }

    public Uni<WorkflowResource> findByDefinitionKey(String definitionKey) {
        return find("definitionKey", definitionKey).firstResult();
    }
}
