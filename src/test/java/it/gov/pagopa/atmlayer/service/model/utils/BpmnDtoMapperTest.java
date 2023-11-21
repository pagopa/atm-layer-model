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
public class BpmnDtoMapperTest {
    @Inject
    private BpmnDtoMapper bpmnDtoMapper; // Sostituisci con il nome della tua classe di servizio

    @Test
    public void testCalculateSha256() throws IOException, NoSuchAlgorithmException {
        // Preparazione del file di test
        File testFile = createTestFile(); // Implementa questa funzione secondo le tue esigenze

        // Esegui il metodo da testare
        String calculatedHash = bpmnDtoMapper.calculateSha256(testFile);

        // Calcola manualmente l'hash SHA-256 del file di test (ad esempio, usando un'altra libreria)
        String expectedHash = calculateExpectedHash(testFile); // Implementa questa funzione secondo le tue esigenze

        // Verifica che il risultato sia corretto
        assertEquals(expectedHash, calculatedHash);
    }

    // Metodo di supporto per creare un file di test
    public File createTestFile() throws IOException {
        File testFile = File.createTempFile("testfile", ".txt");

        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write("Hello, Quarkus Test".getBytes());
        }

        return testFile;
    }

    // Metodo di supporto per calcolare manualmente l'hash SHA-256 del file di test
    public String calculateExpectedHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] hashBytes = digest.digest(fileBytes);

        // Converti l'array di byte in una stringa esadecimale
        StringBuilder hashStringBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            hashStringBuilder.append(String.format("%02x", b));
        }

        return hashStringBuilder.toString();
    }
}
