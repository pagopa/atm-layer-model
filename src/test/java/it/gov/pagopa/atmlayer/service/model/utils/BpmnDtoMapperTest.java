package it.gov.pagopa.atmlayer.service.model.utils;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.assertEquals;
@QuarkusTest
class BpmnDtoMapperTest {
    @Inject
    BpmnDtoMapper bpmnDtoMapper;

    @Test
    void testCalculateSha256() throws IOException, NoSuchAlgorithmException {
        File testFile = createTestFile();
        String calculatedHash = BpmnDtoMapper.calculateSha256(testFile);
        String expectedHash = calculateExpectedHash(testFile);
        assertEquals(expectedHash, calculatedHash);
    }

    public File createTestFile() throws IOException {
        File testFile = File.createTempFile("testfile", ".txt");
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write("Hello, Quarkus Test".getBytes());
        }

        return testFile;
    }


    public String calculateExpectedHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] hashBytes = digest.digest(fileBytes);
        StringBuilder hashStringBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            hashStringBuilder.append(String.format("%02x", b));
        }
        return hashStringBuilder.toString();
    }
}
