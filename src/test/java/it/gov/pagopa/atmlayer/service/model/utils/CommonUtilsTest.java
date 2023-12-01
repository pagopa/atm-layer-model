package it.gov.pagopa.atmlayer.service.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.nio.file.FileSystems;

@QuarkusTest
class CommonUtilsTest {

  @Test
  void testGetPathWithoutFilename() {
    assertEquals("Pat", CommonUtils.getPathWithoutFilename("Path"));
  }

  @Test
  void testGetFilenameWithExtension() {
    assertEquals("foo.txt.0123456789ABCDEF",
        CommonUtils.getFilenameWithExtension("foo.txt", "0123456789ABCDEF"));
  }

  @Test
  void testGetFilenameWithExtensionFromStorageKey() {
    assertEquals("0123456789ABCDEF.",
        CommonUtils.getFilenameWithExtensionFromStorageKey("0123456789ABCDEF"));
    assertEquals(".", CommonUtils.getFilenameWithExtensionFromStorageKey("."));
  }

  @Test
  void testGetRelativePath() {
    assertEquals(".." + FileSystems.getDefault().getSeparator() +
        "Absolute Path", CommonUtils.getRelativePath("Base Path", "Absolute Path"));
  }
}
