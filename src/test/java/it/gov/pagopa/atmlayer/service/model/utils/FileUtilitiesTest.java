package it.gov.pagopa.atmlayer.service.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

@QuarkusTest
public class FileUtilitiesTest {

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
