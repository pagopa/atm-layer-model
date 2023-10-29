package it.gov.pagopa.atmlayer.service.model.validators;

import com.google.common.collect.Sets;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BpmnEntityValidator {

    BpmnVersionService bpmnVersionService;

    @Inject
    public BpmnEntityValidator(BpmnVersionService bpmnVersionService) {
        this.bpmnVersionService = bpmnVersionService;
    }

    public Uni<Void> validateExistence(Set<BpmnVersionPK> ids) {
        return this.bpmnVersionService.findByPKSet(ids)
                .onItem()
                .invoke(Unchecked.consumer(list -> {
                    if (list.isEmpty() || list.size() != ids.size()) {
                        Set<BpmnVersionPK> extractedKeys = list.stream().map(BpmnVersion::getBpmnVersionPK).collect(Collectors.toSet());
                        Set<BpmnVersionPK> missingBpm = Sets.difference(ids, extractedKeys);
                        String errorMessage = String.format("One or some of the referenced BPMN files do not exists: %s", missingBpm);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.FILE_DOES_NOT_EXIST);
                    }
                }))
                .onItem()
                .transformToUni(t -> Uni.createFrom().nullItem());
    }
}
