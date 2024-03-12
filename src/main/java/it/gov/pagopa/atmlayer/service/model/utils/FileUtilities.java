package it.gov.pagopa.atmlayer.service.model.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.CANNOT_EXTRACT_FILE_DEFINITION_KEY;

@ApplicationScoped
@Slf4j
public class FileUtilities {

    public static String extractIdValue(File file, DeployableResourceType resourceTypeEnum) {
        switch (resourceTypeEnum) {
            case BPMN, DMN -> {
                return extractIdValueFromXML(file, resourceTypeEnum);
            }
            case FORM -> {
                return extractIdValueFromJson(file, resourceTypeEnum);
            }
            default ->
                    throw new AtmLayerException("File non supportato", Response.Status.NOT_ACCEPTABLE, CANNOT_EXTRACT_FILE_DEFINITION_KEY);
        }
    }

    public static String extractIdValueFromXML(File file, DeployableResourceType resourceTypeEnum) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AtmLayerException(Response.Status.BAD_REQUEST, CANNOT_EXTRACT_FILE_DEFINITION_KEY);
        }
        Element definitionsElement = (Element) document.getElementsByTagName(resourceTypeEnum.getTagName()).item(0);
        if (definitionsElement == null) {
            throw new AtmLayerException(Response.Status.BAD_REQUEST, CANNOT_EXTRACT_FILE_DEFINITION_KEY);
        }
        String definitionKey = definitionsElement.getAttribute(resourceTypeEnum.getAttribute());
        if (definitionKey.isBlank()) {
            throw new AtmLayerException("Fallito a trovare il file della chiave di definizione", Response.Status.NOT_ACCEPTABLE, BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY);
        }
        return definitionKey;
    }

    public static String extractIdValueFromJson(File file, DeployableResourceType resourceTypeEnum) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(file);
            return jsonNode.get(resourceTypeEnum.getTagName()).asText();
        } catch (Exception e) {
            throw new AtmLayerException(Response.Status.BAD_REQUEST, CANNOT_EXTRACT_FILE_DEFINITION_KEY);
        }
    }

    public static String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        byte[] array = toSha256ByteArray(file);
        return toHexString(array);
    }

    public static byte[] toSha256ByteArray(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(FileUtils.readFileToByteArray(file));
    }

    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

//    public static boolean isExtensionValid(File file, String fileName) throws IOException, MimeTypeException {
//        String detectedExtension = getExtension(file);
//        String extension = FilenameUtils.getExtension(fileName);
//        if (Objects.equals(extension, "bpmn") || Objects.equals(extension, "dmn")) {
//            extension = UtilityValues.XML_EXTENSION.getValue();
//        }
//        if (Objects.equals(extension, "form")) {
//            extension = UtilityValues.TXT_EXTENSION.getValue();
//        }
//        return Objects.equals(extension, detectedExtension);
//    }
//
//    public static String getExtension(File file) throws IOException, MimeTypeException {
//        Tika tika = new Tika();
//        String mimeType = tika.detect(file);
//        log.info("Detected mimeType: {}", mimeType);
//        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
//        MimeType type = allTypes.forName(mimeType);
//        return type.getExtension().replace(".", "");
//    }

}
