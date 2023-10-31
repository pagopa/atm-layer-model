package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.S3ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.utils.FileStorageS3Utils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;

@ApplicationScoped
@Slf4j
public class S3ObjectStoreServiceImpl implements S3ObjectStoreService {

    @Inject
    S3AsyncClient s3;

    @Inject
    FileStorageS3Utils fileStorageS3Utils;

    @Inject
    S3Presigner presigner;

    @Override
    public ObjectStoreStrategyEnum getType() {
        return ObjectStoreStrategyEnum.AWS_S3;
    }


    @Override
    public Uni<URL> generatePresignedUrl(String objectKey) {
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectRequest getObjectRequest = fileStorageS3Utils.buildGetRequest(objectKey);

            // Genera un URL prefirmato per l'oggetto
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getObjectPresignRequest);
            String myURL = presignedRequest.url().toString();
            log.info("Presigned URL to upload a file to: [{}]", myURL);

            return Uni.createFrom().item(presignedRequest.url());
        }
    }

    public Uni<PutObjectResponse> uploadFile(File file, String path, ResourceTypeEnum fileType, String filename) {
        if (StringUtils.isBlank(filename)) {
            String errorMessage = String.format("S3 File Upload - invalid filename %s", filename);
            log.error(errorMessage);
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
        }

        if (StringUtils.isBlank(path)) {
            String errorMessage = String.format("S3 File Upload - invalid path %s", path);
            log.error(errorMessage);
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
        }

        if (Objects.isNull(file)) {
            String errorMessage = String.format("S3 File Upload - invalid NULL file %s", filename);
            log.error(errorMessage);
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
        }

        return Uni.createFrom().future(() -> {
                    PutObjectRequest putObjectRequest = fileStorageS3Utils.buildPutRequest(filename, fileType.getMimetype(), path);
                    return s3.putObject(putObjectRequest, AsyncRequestBody.fromFile(file));
                })
                .onFailure().transform(error -> {
                    String errorMessage = "Error in uploading file to S3";
                    log.error(errorMessage, error);
                    return new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
                });
    }
}
