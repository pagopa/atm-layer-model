package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
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

    public Uni<Void> deleteByFileName(String fileName) {
        Uni<List<ResourceFile>> records = find("select resource from ResourceFile resource where resource.fileName LIKE :fileName", Parameters.with("fileName",fileName)).list();
        return records.onItem()
                .transformToUni(resourceFiles -> {
                    for (ResourceFile resourceFile : resourceFiles) {
                        resourceFile.delete();
                    }
                    return Uni.createFrom().voidItem();
                });
    }

    public Uni<List<ResourceFile>> findByFileName(String fileName) {
        return find("select resource from ResourceFile resource where resource.fileName =: fileName",
                Parameters.with("fileName", fileName)).list();
    }

    public Uni<Void> deleteByBPMNIdList(List<UUID> uuids) {
        return ResourceFile.delete("bpmn.bpmnId IN (?1)", uuids).replaceWithVoid();
    }
}
