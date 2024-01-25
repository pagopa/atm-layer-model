package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class BpmnVersionRepository implements PanacheRepositoryBase<BpmnVersion, BpmnVersionPK> {
    public Uni<List<BpmnVersion>> findByFilters(Map<String,Object> params, int pageIndex, int pageSize) {
        String queryFilters = params.keySet().stream().map(key -> {
                if (Objects.equals(key, "modelVersion") || Objects.equals(key, "definitionVersionCamunda")) {
                    return ("b." + key + " = :"+key);
        } else {
                    return ("b." + key + " LIKE concat(concat('%', :"+key+"), '%')");}
        }).collect(Collectors.joining(" and "));
        return find(("select b from BpmnVersion b ").concat(queryFilters.isBlank()?"":" where " + queryFilters), params).page(Page.of(pageIndex,pageSize)).list();
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
                Parameters.with("bpmnId", uuid).and("functionType", functionType)).list();
    }

    public Uni<BpmnVersion> findByDefinitionKey(String definitionKey) {
        return find("select b from BpmnVersion b where b.definitionKey = :definitionKey", Parameters.with("definitionKey", definitionKey)).firstResult();
    }
}
