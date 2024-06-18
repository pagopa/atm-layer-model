package it.gov.pagopa.atmlayer.service.model.utils;

import io.quarkus.logging.Log;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
<<<<<<< HEAD
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
=======
import lombok.extern.slf4j.Slf4j;
>>>>>>> 28c3ab7108f2086b53238f1f583be41f0827caa4
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class FileStorageS3Utils {
    @Inject
    ObjectStoreProperties objectStoreProperties;

    public PutObjectRequest buildPutRequest(String filename, String mimetype, String path) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", mimetype);
        return PutObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(path.concat("/").concat(filename))
                .contentType(mimetype)
                .metadata(metadata)
                .build();
    }

    public GetObjectRequest buildGetRequest(String key) {
        return GetObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(key)
                .build();
    }
    public CopyObjectRequest buildCopyRequest(String sourceKey, String destinationKey, String mimetype) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", mimetype);

        return CopyObjectRequest.builder()
                .sourceKey(sourceKey)
                .sourceBucket(objectStoreProperties.bucket().name())
                .destinationBucket(objectStoreProperties.bucket().name())
                .destinationKey(destinationKey)
                .metadata(metadata)
                .build();

    }
    public static String modifyPath(String inputPath) {
        Path path = Paths.get(inputPath);
        log.info("valore input: {}", inputPath);

        int count = path.getNameCount();
        StringBuilder outputPath = new StringBuilder();

        outputPath.append(path.getRoot() != null ? path.getRoot() : ""); // se esiste una root (es. C:\), aggiungila
        outputPath.append(path.getName(0));
        outputPath.append("/");
        outputPath.append(path.getName(1));
        outputPath.append("/");
        outputPath.append(path.getName(2));
        outputPath.append("/DELETE");

        for (int i = 3; i < count - 1; i++) {
            outputPath.append("/");
            outputPath.append(path.getName(i));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        outputPath.append("/");
        outputPath.append(timestamp);

        outputPath.append("/");
        outputPath.append(path.getFileName().toString());

        log.info("valore output: {}", outputPath);
        return outputPath.toString();
    }

}
