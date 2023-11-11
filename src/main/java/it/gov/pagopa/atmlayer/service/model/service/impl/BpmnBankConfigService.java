package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnBankConfigRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class BpmnBankConfigService {
    @Inject
    protected BpmnBankConfigRepository bankConfigRepository;

    @Inject
    BpmnConfigMapper bpmnConfigMapper;

    @WithTransaction
    public Uni<Void> saveList(List<BpmnBankConfig> bpmnBankConfigs) {
        return bankConfigRepository.persist(bpmnBankConfigs);
    }

    public Uni<List<BpmnBankConfig>> findByAcquirerIdAndFunctionType(String acquirerId, String functionType) {
        return this.bankConfigRepository.findByAcquirerIdAndFunctionType(acquirerId, functionType);
    }

    @WithTransaction
    public Uni<Long> deleteByAcquirerIdAndFunctionType(String acquirerId, String functionTypeEnum) {
        return this.bankConfigRepository.deleteByAcquirerIdAndFunctionType(acquirerId, functionTypeEnum);
    }

    public Uni<Optional<BpmnBankConfig>> findByConfigurationsAndFunction(String acquirerId, String branchId, String terminalId, String functionTypeEnum) {
        return this.bankConfigRepository.findByConfigAndFunctionType(acquirerId, branchId, terminalId, functionTypeEnum)
                .onItem().transformToUni(Unchecked.function(x -> {
                    if (!x.isEmpty() && x.size() > 1) {
                        throw new AtmLayerException("Multiple BPMN found for a single configuration.", Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.ATMLM_500);
                    }

                    return Uni.createFrom().item(x.isEmpty() ? Optional.empty() : Optional.ofNullable(x.get(0)));
                }));
    }

    public Uni<List<BpmnBankConfigDTO>> findByAcquirerId(String acquirerId) {
        return bankConfigRepository.findByAcquirerId(acquirerId)
                .onItem()
                .transformToUni(Unchecked.function(configs -> {
                    if (configs.isEmpty()) {
                        throw new AtmLayerException("No BPMN configurations found for this bank", Response.Status.NOT_FOUND, AppErrorCodeEnum.NO_CONFIGURATION_FOR_ACQUIRER);
                    } else {
                        return Uni.createFrom().item(
                                configs.stream()
                                        .map(bpmnConfigMapper::toDTO)
                                        .collect(Collectors.toList())
                        );
                    }
                }));
    }
}
