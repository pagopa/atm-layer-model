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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@QuarkusTest
public class FileUtilitiesTest {

  @Test
  public void testExtractIdValueFromXMLBlankDefinitionKey() {

    File file = new File("path/to/your/xml/file.xml");
    DeployableResourceType resourceType = DeployableResourceType.BPMN;

    Document documentMock = mock(Document.class);
    NodeList mockList = mock(NodeList.class);
    Element element = mock(Element.class);


    when(documentMock.getElementsByTagName(anyString())).thenReturn(mockList);
    when(mockList.item(0)).thenReturn(element);
    when(element.getAttribute(anyString())).thenReturn("");

    assertThrows(
        AtmLayerException.class, () -> FileUtilities.extractIdValueFromXML(file, resourceType));
  }

  @Test
  public void testCalculateSha256HashLength() throws IOException, NoSuchAlgorithmException {
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
}
