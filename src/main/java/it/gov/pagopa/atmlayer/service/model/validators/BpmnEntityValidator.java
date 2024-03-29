package it.gov.pagopa.atmlayer.service.model.validators;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BpmnEntityValidator {

    BpmnVersionService bpmnVersionService;

    @Inject
    public BpmnEntityValidator(BpmnVersionService bpmnVersionService) {
        this.bpmnVersionService = bpmnVersionService;
    }

    public Uni<Void> validateExistenceStatusAndFunctionType(Set<BpmnVersionPK> ids, String functionType) {
        return this.bpmnVersionService.findByPKSet(ids)
                .onItem()
                .invoke(Unchecked.consumer(list -> {
                    Set<BpmnVersionPK> extractedKeys = list.stream().map(x -> new BpmnVersionPK(x.getBpmnId(), x.getModelVersion())).collect(Collectors.toSet());
                    Set<BpmnVersion> notDeployedBpmnFiles = list.stream().filter(bpmnVersion -> !bpmnVersion.getStatus().equals(StatusEnum.DEPLOYED)).collect(Collectors.toSet());
                    if (!CollectionUtils.isNullOrEmpty(notDeployedBpmnFiles)) {
                        String errorMessage = String.format("Uno o alcuni dei file BPMN a cui si fa riferimento non sono rilascati: %s", notDeployedBpmnFiles.stream().map(x -> new BpmnVersionPK(x.getBpmnId(), x.getModelVersion())).collect(Collectors.toSet()));
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.BPMN_FILE_NOT_DEPLOYED);
                    }
                    Set<BpmnVersion> notValidFunctionTypeBpmnFiles = list.stream().filter(bpmnVersion -> !bpmnVersion.getFunctionType().equals(functionType)).collect(Collectors.toSet());
                    if (!CollectionUtils.isNullOrEmpty(notValidFunctionTypeBpmnFiles)) {
                        String errorMessage = String.format("Uno o alcuni dei file BPMN a cui si fa riferimento non hanno tipo di funzione %s: %s", functionType, notValidFunctionTypeBpmnFiles.stream().map(x -> new BpmnVersionPK(x.getBpmnId(), x.getModelVersion())).collect(Collectors.toSet()));
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.BPMN_FUNCTION_TYPE_DIFFERENT_FROM_REQUESTED);
                    }
                    Set<BpmnVersionPK> missingBpmn = ids.stream().filter(e -> !extractedKeys.contains(e)).collect(Collectors.toSet());
                    if (!CollectionUtils.isNullOrEmpty(missingBpmn)) {
                        String errorMessage = String.format("Uno o alcuni dei file BPMN a cui si fa riferimento non esistono: %s", missingBpmn);
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST);
                    }

                }))
                .onItem()
                .transformToUni(t -> Uni.createFrom().nullItem());
    }
}
