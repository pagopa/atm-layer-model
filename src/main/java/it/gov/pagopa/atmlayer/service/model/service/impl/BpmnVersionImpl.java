package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnVersionRepository;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.utils.ModelUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
@Slf4j
public class BpmnVersionImpl implements BpmnVersionService {

    @Inject
    BpmnVersionRepository bpmnVersionRepository;

    @Override
    public String decodeBase64(String s) throws IOException {
        byte[] array = ModelUtils.base64ToByteArray(s);
        return ModelUtils.byteArrayToString(array);
    }

    @Override
    public String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        //TODO: Controllare che il file sia un xml .bpmn
        byte[] array = ModelUtils.toSha256ByteArray(file);
        return ModelUtils.toHexString(array);
    }

    @Override
    @WithTransaction
    public Uni<BpmnVersion> save(BpmnVersion bpmnVersion) {
        return bpmnVersionRepository.persist(bpmnVersion);
    }

    @Override
    @WithSession
    public Uni<BpmnVersion> findByPk(BpmnVersionPK bpmnVersionPK) {
        return bpmnVersionRepository.findById(bpmnVersionPK);
    }
}
