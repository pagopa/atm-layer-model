package it.gov.pagopa.atmlayer.service.model.utils;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class BpmnDtoMapper {

    public static String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        byte[] array = FileUtilities.toSha256ByteArray(file);
        return FileUtilities.toHexString(array);
    }
}
