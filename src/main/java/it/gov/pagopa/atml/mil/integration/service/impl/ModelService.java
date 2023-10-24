package it.gov.pagopa.atml.mil.integration.service.impl;

import it.gov.pagopa.atml.mil.integration.utils.ModelUtils;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
@Slf4j
public class ModelService {

    public String decodeBase64(String s) throws IOException {
        byte[] array = ModelUtils.base64ToByteArray(s);
        String result = ModelUtils.byteArrayToString(array);
        return result;
    }

    public String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        byte[] array = ModelUtils.toSha256ByteArray(file);
        return ModelUtils.toHexString(array);
    }
}
