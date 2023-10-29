package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class BpmnVersionRepository implements PanacheRepositoryBase<BpmnVersion, BpmnVersionPK> {
    public Uni<BpmnVersion> findBySHA256(String sha256) {
        return find("sha256", sha256).firstResult();
    }

    public Uni<List<BpmnVersion>> findByIds(Set<BpmnVersionPK> ids) {
        return find("where bpmnVersionPK in ?1", ids).list();
    }

}
