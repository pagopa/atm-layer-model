package it.gov.pagopa.atmlayer.service.model.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.atmlayer.service.model.configurations.DirManager;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ATMLM_500;
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

    public static File fromStringToFile(String fileBase64) {
        if (!DirManager.decodedFilesDirectory.exists()) {
            throw new AtmLayerException("Impossibile convertire i file in input: non è stata creata una directory sicura per il salvataggio di file temporanei.", Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.ATMLM_500);
        }
        File tempFile = null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(fileBase64);
            if (SystemUtils.IS_OS_UNIX) {
                Set<PosixFilePermission> filePermissions = EnumSet.of(
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE,
                        PosixFilePermission.OWNER_EXECUTE
                );
                tempFile = File.createTempFile("tempfile", ".tmp", DirManager.decodedFilesDirectory);
                java.nio.file.Files.setPosixFilePermissions(tempFile.toPath(), filePermissions);
            } else {
                tempFile = File.createTempFile("tempfile", ".tmp", DirManager.decodedFilesDirectory);
                boolean readable = tempFile.setReadable(true, true);
                boolean writable = tempFile.setWritable(true, true);
                boolean executable = tempFile.setExecutable(true, true);
                if (!readable || !writable || !executable) {
                    throw new IOException("Impossibile impostare i permessi di sicurezza sul file temporaneo.");
                }
            }
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(decodedBytes);
            }
            return tempFile;
        } catch (IllegalArgumentException e) {
            log.error("Errore nella decodifica del Base64: " + e.getMessage());
            throw new AtmLayerException("Errore nella decodifica del File Base64", Response.Status.NOT_ACCEPTABLE, AppErrorCodeEnum.FILE_DECODE_ERROR);
        } catch (IOException e) {
            log.error("Errore nella scrittura del file: " + e.getMessage());
            throw new AtmLayerException("Errore nella scrittura del file", Response.Status.NOT_ACCEPTABLE, AppErrorCodeEnum.FILE_DECODE_ERROR);
        }
    }

    public static void cleanDecodedFilesDirectory() {
        try {
            FileUtils.cleanDirectory(DirManager.decodedFilesDirectory);
        } catch (IOException e) {
            throw new AtmLayerException("Errore nell'eliminazione dei file temporanei", Response.Status.INTERNAL_SERVER_ERROR, ATMLM_500);
        }
    }

}
