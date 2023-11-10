package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ResourceEntityRepository implements PanacheRepositoryBase<ResourceEntity, UUID> {
    public Uni<ResourceEntity> findBySHA256(String sha256) {
        return find("sha256", sha256).firstResult();
    }
}
