package it.gov.pagopa.atmlayer.service.model.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.MALFORMED_FILE;

@ApplicationScoped
@Slf4j
public class FileUtils {

    public static String extractIdValue(File file, DeployableResourceType resourceTypeEnum) {
        switch (resourceTypeEnum) {
            case BPMN, DMN -> {
                return extractIdValueFromXML(file, resourceTypeEnum);
            }
            case FORM -> {
                return extractIdValueFromJson(file, resourceTypeEnum);
            }
            default -> throw new AtmLayerException("File not supported", Response.Status.NOT_ACCEPTABLE, MALFORMED_FILE);
        }
    }

    public static String extractIdValueFromXML(File file, DeployableResourceType resourceTypeEnum) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error(e.getMessage());
            throw new AtmLayerException("Malformed File", Response.Status.BAD_REQUEST, MALFORMED_FILE);
        }
        Element definitionsElement = (Element) document.getElementsByTagName(resourceTypeEnum.getTagName()).item(0);
        String definitionKey = definitionsElement.getAttribute(resourceTypeEnum.getAttribute());
        if (definitionKey.isBlank()) {
            throw new AtmLayerException("Failed to find definition key file", Response.Status.NOT_ACCEPTABLE, BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY);
        }
        return definitionKey;
    }

    public static String extractIdValueFromJson(File file, DeployableResourceType resourceTypeEnum) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(file);
            return jsonNode.get(resourceTypeEnum.getTagName()).asText();
        } catch (Exception e) {
            throw new AtmLayerException("Malformed File", Response.Status.BAD_REQUEST, MALFORMED_FILE);
        }
    }

    public static byte[] fileToByteArray(File file) throws IOException {
        return Files.toByteArray(file);
    }

    public static String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        byte[] array = toSha256ByteArray(file);
        return toHexString(array);
    }

    public static byte[] encodeToBase64(byte[] array) {
        return Base64.getEncoder().encode(array);
    }

    public static byte[] toSha256ByteArray(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(Files.toByteArray(file));
    }

    public static byte[] base64ToByteArray(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static String byteArrayToString(byte[] byteArray) {
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}
