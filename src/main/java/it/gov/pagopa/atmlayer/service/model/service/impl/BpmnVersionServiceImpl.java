package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.functionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.BpmnIdDto;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnVersionRepository;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.OBJECT_STORE_SAVE_FILE_ERROR;

@ApplicationScoped
@Slf4j
public class BpmnVersionServiceImpl implements BpmnVersionService {

    @Inject
    BpmnVersionRepository bpmnVersionRepository;

    @Inject
    BpmnBankConfigService bpmnBankConfigService;

    @Inject
    BpmnFileStorageService bpmnFileStorageService;

    @Inject
    ThreadContext threadContext;
    @Inject
    ManagedExecutor managedExecutor;

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
        byte[] array = BpmnUtils.toSha256ByteArray(file);
        return BpmnUtils.toHexString(array);
    }

    @Override
    @WithTransaction
    public Uni<BpmnVersion> save(BpmnVersion bpmnVersion) {
        log.info("checking that no already existing file with sha256 {} exist", bpmnVersion.getSha256());
        return this.findBySHA256(bpmnVersion.getSha256())
                .onItem().transform(Unchecked.function(x -> {
                    if (x.isPresent()) {
                        throw new AtmLayerException("A BPMN file with the same content already exists", Response.Status.BAD_REQUEST, BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST);
                    }
                    return x;
                }))
                .onItem().transformToUni(t -> {
                    log.info("Persisting bpmn {} to database", bpmnVersion.getDeployedFileName());
                    return this.bpmnVersionRepository.persist(bpmnVersion);
                });
    }

    @WithTransaction
    @Override
    public Uni<Boolean> delete(BpmnVersionPK bpmnVersionPK) {
        log.info("Deleting BPMN with id {}", bpmnVersionPK.toString());
        return this.bpmnVersionRepository.deleteById(bpmnVersionPK);
    }

    @Override
    public Uni<Optional<BpmnVersion>> findBySHA256(String sha256) {
        return this.bpmnVersionRepository.findBySHA256(sha256)
                .onItem().transformToUni(x -> Uni.createFrom().item(Optional.ofNullable(x)));
    }

    @Override
    public Uni<Optional<BpmnVersion>> findByPk(BpmnVersionPK bpmnVersionPK) {
        return bpmnVersionRepository.findById(bpmnVersionPK).onItem().transformToUni(bpmnVersion -> Uni.createFrom().item(Optional.ofNullable(bpmnVersion)));
    }

    @Override
    @WithTransaction
    public Uni<List<BpmnBankConfig>> putAssociations(String acquirerId, functionTypeEnum functionType, List<BpmnBankConfig> bpmnBankConfigs) {
        Uni<Long> deleteExistingUni = this.bpmnBankConfigService.deleteByAcquirerIdAndFunctionType(acquirerId, functionType);
        return deleteExistingUni
                .onItem()
                .transformToUni(x -> this.bpmnBankConfigService.saveList(bpmnBankConfigs))
                .onItem()
                .transformToUni(y -> this.bpmnBankConfigService.findByAcquirerIdAndFunctionType(acquirerId, functionType));
    }


    @Override
    public Uni<BpmnVersion> saveAndUpload(BpmnVersion bpmnVersion, File file, String filename) {
        Context context = Vertx.currentContext();
        return this.save(bpmnVersion)
                .onItem().transformToUni(record -> {
                    return this.bpmnFileStorageService.uploadFile(new BpmnIdDto(record.getBpmnId(), record.getModelVersion()), file, filename)
                            .emitOn(command -> context.runOnContext(x -> command.run()))
                            .onFailure().recoverWithUni(failure -> {
                                // If upload fails, delete the BpmnVersion
                                return this.delete(new BpmnVersionPK(record.getBpmnId(), record.getModelVersion()))
                                        .onFailure().recoverWithUni(exceptionOnRollback -> {
                                            return Uni.createFrom().failure(new AtmLayerException("Failed to write File on Object Store and attempt to recover failed too. Bpmn Entity could not be rollbacked", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                                        })
                                        .onItem().transformToUni(deleteResult -> {
                                            // Create an exception and propagate it
                                            return Uni.createFrom().failure(new AtmLayerException("Failed to write File on Object Store. Bpmn Entity has been rollbacked", Response.Status.INTERNAL_SERVER_ERROR, OBJECT_STORE_SAVE_FILE_ERROR));
                                        });
                            })
                            .onItem().transformToUni(putObjectResponse -> Uni.createFrom().item(record));
                });
    }

    private static Uni<List<BpmnBankConfig>> addFunctionTypeToAssociations(List<BpmnBankConfig> bpmnBankConfigs, functionTypeEnum functionType) {
        return Multi.createFrom().items(bpmnBankConfigs.stream()).onItem().transform(bpmnBankConfig -> {
            bpmnBankConfig.setFunctionType(functionType);
            return bpmnBankConfig;
        }).collect().asList();
    }
}
