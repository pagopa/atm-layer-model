package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BpmnBankConfigRepository implements PanacheRepositoryBase<BpmnBankConfig, BpmnBankConfigPK> {
}
