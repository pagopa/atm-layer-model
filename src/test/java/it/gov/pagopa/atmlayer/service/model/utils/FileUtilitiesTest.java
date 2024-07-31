package it.gov.pagopa.atmlayer.service.model.utils;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import static it.gov.pagopa.atmlayer.service.model.utils.FileUtilities.fromStringToFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class FileUtilitiesTest {

    @Test
    void testFromStringToFile_ValidBase64() throws IOException {
        String validBase64 = Base64.getEncoder().encodeToString("Hello World!".getBytes());
        String fileName = "filename";

        File result = fromStringToFile(validBase64, fileName);

        assertNotNull(result);
        assertTrue(result.exists());

        byte[] fileContent = Files.readAllBytes(result.toPath());
        assertEquals("Hello World!", new String(fileContent));

        result.delete();
    }

    @Test
    void testFromStringToFile_InvalidBase64() {
        String invalidBase64 = "Invalid@@Base64###";
        String fileName = "filename";

        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> fromStringToFile(invalidBase64, fileName));

        assertEquals("Errore nella decodifica del File Base64", exception.getMessage());
        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), exception.getStatusCode());
        assertEquals(AppErrorCodeEnum.FILE_DECODE_ERROR.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testExtractIdValueFromXMLBlankDefinitionKey() {
        File file = new File("src/test/resources/TestMalformed.bpmn");
        DeployableResourceType resourceType = DeployableResourceType.BPMN;
        Element element = mock(Element.class);
        when(element.getAttribute(anyString())).thenReturn("");
        assertThrows(
                AtmLayerException.class, () -> FileUtilities.extractIdValueFromXML(file, resourceType));
    }

    @Test
    void testCalculateSha256HashLength() throws IOException, NoSuchAlgorithmException {
        File tempFile = createTempFileWithRandomContent();
        String hash = FileUtilities.calculateSha256(tempFile);
        assertEquals(64, hash.length());
    }

    private File createTempFileWithRandomContent() throws IOException {
        Path tempFilePath = Files.createTempFile("testFile", ".txt");
        File tempFile = tempFilePath.toFile();
        byte[] randomBytes = new byte[1024];
        new Random().nextBytes(randomBytes);
        Files.write(tempFilePath, randomBytes);
        return tempFile;
    }

    @Test
    void testExtractIdValueFromJSONBlankDefinitionKey() {
        File file = new File("src/test/resources/TestMalformed.form");
        DeployableResourceType resourceType = DeployableResourceType.FORM;
        Element element = mock(Element.class);
        when(element.getAttribute(anyString())).thenReturn("");
        assertThrows(
                AtmLayerException.class, () -> FileUtilities.extractIdValue(file, resourceType));
    }
}
