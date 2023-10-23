package it.gov.pagopa.atml.mil.integration.service.impl;

import it.gov.pagopa.atml.mil.integration.utils.ModelUtils;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
@Slf4j
public class ModelService {

    public String decodeBase64 (String s) throws IOException {
        byte [] array = ModelUtils.base64ToByteArray(s);
        return ModelUtils.byteArrayToString(array);
    }
}
