package it.gov.pagopa.atml.mil.integration.utils;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.Base64;

@ApplicationScoped
public class ModelUtils {

    public static String base64Calculator(byte [] stream) {
        return Base64.getEncoder().encodeToString(stream);
    }

    public static byte [] base64ToByteArray(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static String byteArrayToString (byte [] byteArray) {
        return Arrays.toString(byteArray);
    }

}
