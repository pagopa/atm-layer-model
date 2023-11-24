package it.gov.pagopa.atmlayer.service.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.nio.file.FileSystems;

@QuarkusTest
public class CommonUtilsTest {

  @Test
  public void testGetPathWithoutFilename() {
    assertEquals("Pat", CommonUtils.getPathWithoutFilename("Path"));
  }

  @Test
  public void testGetFilenameWithExtension() {
    assertEquals("foo.txt.0123456789ABCDEF",
        CommonUtils.getFilenameWithExtension("foo.txt", "0123456789ABCDEF"));
  }

  @Test
  public void testGetFilenameWithExtensionFromStorageKey() {
    assertEquals("0123456789ABCDEF.",
        CommonUtils.getFilenameWithExtensionFromStorageKey("0123456789ABCDEF"));
    assertEquals(".", CommonUtils.getFilenameWithExtensionFromStorageKey("."));
  }

  @Test
  public void testGetRelativePath() {
    assertEquals(".." + FileSystems.getDefault().getSeparator() +
        "Absolute Path", CommonUtils.getRelativePath("Base Path", "Absolute Path"));
  }
}
