package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.functionTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BpmnBankConfigRepository implements PanacheRepositoryBase<BpmnBankConfig, BpmnBankConfigPK> {

    public Uni<Long> deleteByAcquirerIdAndFunctionType(String acquirerId, functionTypeEnum functionType) {
        Map<String, Object> params = new HashMap<>();
        params.put("acquirerId", acquirerId);
        params.put("functionType", functionType);
        return delete(
                "delete from BpmnBankConfig b where b.bpmnBankConfigPK.acquirerId = :acquirerId and b.functionType= :functionType", params);
    }

    public Uni<List<BpmnBankConfig>> findByAcquirerIdAndFunctionType(String acquirerId, functionTypeEnum functionType) {
        Map<String, Object> params = new HashMap<>();
        params.put("acquirerId", acquirerId);
        params.put("functionType", functionType);
        return list("select b from BpmnBankConfig b where b.bpmnBankConfigPK.acquirerId = :acquirerId and b.functionType= :functionType", params);
    }
}