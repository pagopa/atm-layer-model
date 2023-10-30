package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnVersionRepository;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST;

@ApplicationScoped
@Slf4j
public class BpmnVersionImpl implements BpmnVersionService {

    @Inject
    BpmnVersionRepository bpmnVersionRepository;

    @Inject
    BpmnBankConfigService bpmnBankConfigService;

    @Override
    public String decodeBase64(String s) {
        byte[] array = BpmnUtils.base64ToByteArray(s);
        return BpmnUtils.byteArrayToString(array);
    }

    @Override
    public Uni<List<BpmnVersion>> findByPKSet(Set<BpmnVersionPK> bpmnVersionPKSet) {
        return this.bpmnVersionRepository.findByIds(bpmnVersionPKSet);
    }

    @Override
    public String calculateSHA256(File file) throws NoSuchAlgorithmException, IOException {
        //TODO: Controllare che il file sia un xml .bpmn
        byte[] array = BpmnUtils.toSha256ByteArray(file);
        return BpmnUtils.toHexString(array);
    }

    @Override
    @WithTransaction
    public Uni<BpmnVersion> save(BpmnVersion bpmnVersion) {
        return this.findBySHA256(bpmnVersion.getSha256())
                .onItem().transform(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException("A BPMN file with the same content already exists", Response.Status.BAD_REQUEST, BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST);
                    }
                    return x;
                }))
                .onItem().transformToUni(t -> this.bpmnVersionRepository.persist(bpmnVersion));
    }

    @Override
    @WithSession
    public Uni<Optional<BpmnVersion>> findBySHA256(String sha256) {
        return this.bpmnVersionRepository.findBySHA256(sha256)
                .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
    }

    @Override
    @WithSession
    public Uni<Optional<BpmnVersion>> findByPk(BpmnVersionPK bpmnVersionPK) {
        return bpmnVersionRepository.findById(bpmnVersionPK).onItem().transformToUni(bpmnVersion -> Uni.createFrom().item(Optional.ofNullable(bpmnVersion)));
    }

    @Override
    @WithTransaction
    public Uni<List<BpmnBankConfig>> putAssociations(String acquirerId, FunctionTypeEnum functionType, List<BpmnBankConfig> bpmnBankConfigs) {
        Uni<Long> deleteExistingUni = this.bpmnBankConfigService.deleteByAcquirerIdAndFunctionType(acquirerId, functionType);
        return deleteExistingUni
                .onItem()
                .transformToUni(x -> this.bpmnBankConfigService.saveList(bpmnBankConfigs))
                .onItem()
                .transformToUni(y -> this.bpmnBankConfigService.findByAcquirerIdAndFunctionType(acquirerId, functionType));
    }

    private static Uni<List<BpmnBankConfig>> addFunctionTypeToAssociations(List<BpmnBankConfig> bpmnBankConfigs, FunctionTypeEnum functionType) {
        return Multi.createFrom().items(bpmnBankConfigs.stream()).onItem().transform(bpmnBankConfig -> {
            bpmnBankConfig.setFunctionType(functionType);
            return bpmnBankConfig;
        }).collect().asList();
    }
}
