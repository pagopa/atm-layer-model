package it.gov.pagopa.atml.mil.integration.service;

import it.gov.pagopa.atml.mil.integration.utils.ModelUtils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ModelService {

    String decodeBase64(String s) throws IOException;

    String calculateSha256(File file) throws NoSuchAlgorithmException, IOException;

}
