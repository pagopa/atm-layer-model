package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.UtilityValues;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
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

    public Uni<List<BpmnBankConfig>> findByBpmnPK(BpmnVersionPK bpmnVersionPK) {
        Map<String, Object> params = new HashMap<>();
        params.put("bpmnId", bpmnVersionPK.getBpmnId());
        params.put("version", bpmnVersionPK.getModelVersion());
        return find("select b from BpmnBankConfig b where b.bpmnBankConfigPK.bpmnId = :bpmnId and b.bpmnBankConfigPK.bpmnModelVersion= :version", params).list();
    }

    public Uni<PageInfo<BpmnBankConfig>> findByBpmnPKPaged(BpmnVersionPK bpmnVersionPK, int pageIndex, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("bpmnId", bpmnVersionPK.getBpmnId());
        params.put("version", bpmnVersionPK.getModelVersion());
        PanacheQuery<BpmnBankConfig> queryResult = find("select b from BpmnBankConfig b where b.bpmnBankConfigPK.bpmnId = :bpmnId and b.bpmnBankConfigPK.bpmnModelVersion= :version", params).page(Page.of(pageIndex, pageSize));
        return queryResult.count()
                .onItem().transformToUni(count -> {
                    int totalCount = count.intValue();
                    int totalPages = (int) Math.ceil((double) totalCount / pageSize);
                    return queryResult.list()
                            .onItem()
                            .transform(list -> new PageInfo<BpmnBankConfig>(pageIndex, pageSize, totalCount, totalPages, list));
                });
    }
}
