package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BpmnVersionRepository implements PanacheRepositoryBase<BpmnVersion, BpmnVersionPK> {

//    Uni<BpmnVersion> findByPK(BpmnVersionPK bpmnVersionPK) {
//        return findByPK(bpmnVersionPK);
//    }

}
