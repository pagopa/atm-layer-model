package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.enumeration.functionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnBankConfigRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class BpmnBankConfigService {
    @Inject
    protected BpmnBankConfigRepository bankConfigRepository;

    @WithTransaction
    public Uni<Void> saveList(List<BpmnBankConfig> bpmnBankConfigs) {
        return bankConfigRepository.persist(bpmnBankConfigs);
    }

    public Uni<List<BpmnBankConfig>> findByAcquirerIdAndFunctionType(String acquirerId, functionTypeEnum functionType){
        return this.bankConfigRepository.findByAcquirerIdAndFunctionType(acquirerId,functionType);
    }

    @WithTransaction
    public Uni<Long> deleteByAcquirerIdAndFunctionType(String acquirerId, functionTypeEnum functionTypeEnum) {
        return this.bankConfigRepository.deleteByAcquirerIdAndFunctionType(acquirerId, functionTypeEnum);
    }
}
