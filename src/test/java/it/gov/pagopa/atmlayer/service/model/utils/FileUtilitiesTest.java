package it.gov.pagopa.atmlayer.service.model.utils;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class FileUtilitiesTest {
    @BeforeAll
    static void initAll() {
    }

    @TempDir
    Path tempDir;
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile(tempDir, "temp", ".xml").toFile();
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }
//    @Test
//    public void extractIdValueOK() {
//        try {
//            String expectedValue = "demo11_06";
//            File file = new File("src/test/resources/Test.bpmn");
//
//
//            String actualValue = extractIdValue(file);
//            assertEquals(expectedValue, actualValue);
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            Assertions.assertFalse(false);
//        }
//    }
//
//    @Test
//    public void extractIdValueKO() {
//        File fileNoKey = new File("src/test/resources/TestMalformed.bpmn");
//        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> {
//            extractIdValue(fileNoKey);
//        });
//        String expectedMessage = "Failed to find definition key in the BPMN file";
//        String actualMessage = exception.getMessage();
//        assert (actualMessage.contains(expectedMessage));
//    }
//
//    @Test
//    public void testExtractIdValueWhenFileWithEmptyDefinitionsTagThenThrowAtmLayerException() throws IOException {
//        String xmlContent = "<bpmn:process id=\"\"></bpmn:process>";
//        Files.write(tempFile.toPath(), xmlContent.getBytes());
//        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> FileUtils.extractIdValue(tempFile));
//        assertEquals(AppErrorCodeEnum.BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY.getErrorCode(), exception.getErrorCode());
//    }
//    @Test
//    public void testExtractIdValueWhenFileWithoutDefinitionsTagThenThrowAtmLayerException() throws IOException {
//        String xmlContent = "<bpmn:other id=\"definitionKey\"></bpmn:other>";
//        Files.write(tempFile.toPath(), xmlContent.getBytes());
//        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> FileUtils.extractIdValue(tempFile));
//        assertEquals(AppErrorCodeEnum.BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY.getErrorCode(), exception.getErrorCode());
//    }
}
