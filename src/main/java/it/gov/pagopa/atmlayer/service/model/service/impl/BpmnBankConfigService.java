package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnBankConfigRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BpmnBankConfigService {
    private final BpmnBankConfigRepository bankConfigRepository;
    private final BpmnConfigMapper bpmnConfigMapper;

    @Inject
    public BpmnBankConfigService(BpmnBankConfigRepository bankConfigRepository, BpmnConfigMapper bpmnConfigMapper){
        this.bankConfigRepository = bankConfigRepository;
        this.bpmnConfigMapper = bpmnConfigMapper;
    }

    @WithTransaction
    public Uni<BpmnBankConfig> save(BpmnBankConfig bpmnBankConfig) {
        return bankConfigRepository.persist(bpmnBankConfig);
    }

    @WithTransaction
    public Uni<Void> saveList(List<BpmnBankConfig> bpmnBankConfigs) {
        return bankConfigRepository.persist(bpmnBankConfigs);
    }

    @WithSession
    public Uni<List<BpmnBankConfig>> findByAcquirerIdAndFunctionType(String acquirerId, String functionType) {
        return this.bankConfigRepository.findByAcquirerIdAndFunctionType(acquirerId, functionType);
    }

    @WithSession
    public Uni<List<BpmnBankConfig>> findByBpmnVersionPK(BpmnVersionPK bpmnVersionPK) {
        return this.bankConfigRepository.findByBpmnPK(bpmnVersionPK);
    }

    @WithSession
    public Uni<PageInfo<BpmnBankConfig>> findByBpmnPKPaged(BpmnVersionPK bpmnVersionPK, int pageIndex, int pageSize) {
        return this.bankConfigRepository.findByBpmnPKPaged(bpmnVersionPK, pageIndex, pageSize);
    }

    @WithTransaction
    public Uni<Long> deleteByAcquirerIdAndFunctionType(String acquirerId, String functionType) {
        return this.bankConfigRepository.deleteByAcquirerIdAndFunctionType(acquirerId, functionType);
    }

    @WithTransaction
    public Uni<Boolean> deleteByBankConfigPK(BpmnBankConfigPK bpmnBankConfigPK) {
        return this.bankConfigRepository.deleteById(bpmnBankConfigPK)
                .onItem().transformToUni(wasDeleted -> {
                    if (Boolean.FALSE.equals(wasDeleted)) {
                        String errorMessage = String.format("Impossibile cancellare la configurazione %s: non esiste oppure si è verificato un errore durante la cancellazione", bpmnBankConfigPK);
                        return Uni.createFrom().failure(new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.CONFIGURATION_DOES_NOT_EXIST));
                    }
                    return Uni.createFrom().item(true);
                });
    }

    @WithSession
    public Uni<Optional<BpmnBankConfig>> findByConfigurationsAndFunction(String acquirerId, String branchId, String terminalId, String functionType) {
        return this.bankConfigRepository.findByConfigAndFunctionType(acquirerId, branchId, terminalId, functionType)
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (x.size() > 1) {
                        throw new AtmLayerException("Sono stati trovati più BPMN per una singola configurazione", Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.ATMLM_500);
                    }
                    return Uni.createFrom().item(x.isEmpty() ? Optional.empty() : Optional.ofNullable(x.get(0)));
                }));
    }

    @WithSession
    public Uni<List<BpmnBankConfigDTO>> findByAcquirerId(String acquirerId) {
        return bankConfigRepository.findByAcquirerId(acquirerId)
                .onItem()
                .transformToUni(Unchecked.function(configs -> {
                    if (configs.isEmpty()) {
                        throw new AtmLayerException("Nessuna configurazione BPMN trovata per questa banca", Response.Status.NOT_FOUND, AppErrorCodeEnum.NO_CONFIGURATION_FOR_ACQUIRER);
                    } else {
                        return Uni.createFrom().item(
                                configs.stream()
                                        .map(bpmnConfigMapper::toDTO)
                                        .toList()
                        );
                    }
                }));
    }
}
