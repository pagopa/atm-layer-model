package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.UtilityValues;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BpmnBankConfigRepository implements PanacheRepositoryBase<BpmnBankConfig, BpmnBankConfigPK> {

    public Uni<Long> deleteByAcquirerIdAndFunctionType(String acquirerId, String functionType) {
        Map<String, Object> params = new HashMap<>();
        params.put(UtilityValues.ACQUIRER_ID.getValue(), acquirerId);
        params.put(UtilityValues.FUNCTION_TYPE.getValue(), functionType);
        return delete(
                "delete from BpmnBankConfig b where b.bpmnBankConfigPK.acquirerId = :acquirerId and b.functionType= :functionType", params);
    }

    public Uni<List<BpmnBankConfig>> findByAcquirerIdAndFunctionType(String acquirerId, String functionType) {
        Map<String, Object> params = new HashMap<>();
        params.put(UtilityValues.ACQUIRER_ID.getValue(), acquirerId);
        params.put(UtilityValues.FUNCTION_TYPE.getValue(), functionType);
        return list("select b from BpmnBankConfig b where b.bpmnBankConfigPK.acquirerId = :acquirerId and b.functionType= :functionType", params);
    }

    public Uni<List<BpmnBankConfig>> findByConfigAndFunctionType(String acquirerId, String branchId, String terminalId, String functionType) {
        Map<String, Object> params = new HashMap<>();
        params.put(UtilityValues.ACQUIRER_ID.getValue(), acquirerId);
        params.put(UtilityValues.BRANCH_ID.getValue(), branchId);
        params.put(UtilityValues.TERMINAL_ID.getValue(), terminalId);
        params.put(UtilityValues.FUNCTION_TYPE.getValue(), functionType);
        return find("select b from BpmnBankConfig b " +
                "where b.bpmnBankConfigPK.branchId= :branchId and b.bpmnBankConfigPK.acquirerId = :acquirerId and b.functionType= :functionType " +
                "and b.bpmnBankConfigPK.terminalId= :terminalId", params).list();
    }

    public Uni<List<BpmnBankConfig>> findByAcquirerId(String acquirerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("acquirerId", acquirerId);
        return find("select b from BpmnBankConfig b where b.bpmnBankConfigPK.acquirerId = :acquirerId", params).list();
    }
}
