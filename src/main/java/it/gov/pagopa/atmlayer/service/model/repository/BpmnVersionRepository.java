package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class BpmnVersionRepository implements PanacheRepositoryBase<BpmnVersion, BpmnVersionPK> {

    private static final String BPMN_ID = "bpmnId";

    public Uni<PageInfo<BpmnVersion>> findByFilters(Map<String, Object> params, int pageIndex, int pageSize) {
        String queryFilters = params.keySet().stream().map(key -> switch (key) {
            case "modelVersion", "definitionVersionCamunda", BPMN_ID, "status" -> ("b." + key + " = :" + key);
            case "acquirerId", "branchId", "terminalId" -> ("lower(bc.bpmnBankConfigPK." + key + ") = lower(:" + key + ")");
            case "fileName" -> ("lower(b.resourceFile." + key + ") LIKE lower(concat(concat(:" + key + "), '%'))");
            default -> ("lower(b." + key + ") LIKE lower(concat(concat(:" + key + "), '%'))");
        }).collect(Collectors.joining(" and "));
        PanacheQuery<BpmnVersion> queryResult = find(("select distinct b from BpmnVersion b").concat(!params.containsKey("acquirerId") ? "" : " join BpmnBankConfig bc on b.bpmnId = bc.bpmnBankConfigPK.bpmnId and b.modelVersion = bc.bpmnBankConfigPK.bpmnModelVersion").concat(queryFilters.isBlank() ? "" : " where " + queryFilters).concat(" order by b.lastUpdatedAt DESC"), params).page(Page.of(pageIndex, pageSize));
        return queryResult.count()
                .onItem().transformToUni(count -> {
                    int totalCount = count.intValue();
                    int totalPages = (int) Math.ceil((double) totalCount / pageSize);
                    return queryResult.list()
                            .onItem()
                            .transform(list -> new PageInfo<BpmnVersion>(pageIndex, pageSize, totalCount, totalPages, list));
                });
    }

    public Uni<BpmnVersion> findBySHA256(String sha256) {
        Map<String, Object> params = new HashMap<>();
        params.put("sha256", sha256);
        return find("select b from BpmnVersion b where b.sha256 = :sha256", params).firstResult();
    }

    public Uni<List<BpmnVersion>> findByIds(Set<BpmnVersionPK> ids) {
        Set<String> bpmnIdVersion = ids.stream()
                .map(x -> x.getBpmnId().toString().concat("_".concat(x.getModelVersion().toString())))
                .collect(Collectors.toSet());
        return find("where concat(bpmnId,'_',modelVersion) in ?1", bpmnIdVersion).list();
    }

    public Uni<List<BpmnVersion>> findByIdAndFunction(UUID uuid, String functionType) {
        return find(
                "select b from BpmnVersion b where b.bpmnId = :bpmnId and b.functionType = :functionType order by b.modelVersion DESC",
                Parameters.with(BPMN_ID, uuid).and("functionType", functionType)).list();
    }

    public Uni<List<BpmnVersion>> findAllByIdAndFunction(UUID uuid, String functionType) {
        String sql = "SELECT * FROM bpmn_version WHERE bpmn_id = :bpmnId AND function_type = :functionType order by model_version DESC";
        return Panache.getSession()
                .onItem()
                .transformToUni(session ->
                        session.createNativeQuery(sql, BpmnVersion.class)
                                .setParameter(BPMN_ID, uuid)
                                .setParameter("functionType", functionType)
                                .getResultList());
    }

    public Uni<List<BpmnVersion>> findAllById(UUID uuid) {
        String sql = "SELECT * FROM bpmn_version WHERE bpmn_id = :bpmnId order by model_version DESC";
        return Panache.getSession()
                .onItem()
                .transformToUni(session ->
                        session.createNativeQuery(sql, BpmnVersion.class)
                                .setParameter(BPMN_ID, uuid)
                                .getResultList());
    }

    public Uni<BpmnVersion> findByDefinitionKey(String definitionKey) {
        return find("select b from BpmnVersion b where b.definitionKey = :definitionKey", Parameters.with("definitionKey", definitionKey)).firstResult();
    }
}
