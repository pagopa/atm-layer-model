package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ResourceFileRepository implements PanacheRepositoryBase<ResourceFile, UUID> {

    public Uni<ResourceFile> findByStorageKey(String key){
        return find(
                "select resource from ResourceFile resource where resource.storageKey = :key",
                Parameters.with("key", key)).firstResult();
    }

    public Uni<ResourceFile> findByResourceId(UUID resourceId){
        return find(
                "select resource from ResourceFile resource where resource.resourceEntity.resourceId = :id",
                Parameters.with("id", resourceId)).firstResult();
    }
}
