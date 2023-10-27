package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface BpmnVersionService {

    String decodeBase64(String s) throws IOException;

    String calculateSha256(File file) throws NoSuchAlgorithmException, IOException;

    Uni<BpmnVersion> save(BpmnVersion bpmnVersion);

    Uni<BpmnVersion> findByPk(BpmnVersionPK bpmnVersionPK);

}
