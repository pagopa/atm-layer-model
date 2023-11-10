package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStorePutResponse;
import it.gov.pagopa.atmlayer.service.model.service.S3ObjectStoreService;
import it.gov.pagopa.atmlayer.service.model.utils.FileStorageS3Utils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.RestMulti;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
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

        GetObjectRequest getObjectRequest = fileStorageS3Utils.buildGetRequest(objectKey);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getObjectPresignRequest);
        String myURL = presignedRequest.url().toString();
        log.info("Generated GET pre-signedUrl [{}]", myURL);

        return Uni.createFrom().item(presignedRequest.url());

    }

    public Buffer toBuffer(ByteBuffer bytebuffer) {
        byte[] result = new byte[bytebuffer.remaining()];
        bytebuffer.get(result);
        return Buffer.buffer(result);
    }

    @Override
    public RestMulti<Buffer> download(String key) {
        return RestMulti.fromUniResponse(Uni.createFrom()
                        .completionStage(() -> s3.getObject(fileStorageS3Utils.buildGetRequest(key),
                                AsyncResponseTransformer.toPublisher())),
                response -> Multi.createFrom().safePublisher(AdaptersToFlow.publisher((Publisher<ByteBuffer>) response))
                        .map(this::toBuffer),
                response -> Map.of("Content-Disposition", List.of("attachment;filename="), "Content-Type",
                        List.of(response.response().contentType())));
    }


    public Uni<ObjectStorePutResponse> uploadFile(File file, String path, S3ResourceTypeEnum fileType, String filename) {
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
        PutObjectRequest putObjectRequest = fileStorageS3Utils.buildPutRequest(filename, fileType.getMimetype(), path);
        return Uni.createFrom().future(() -> s3.putObject(putObjectRequest, AsyncRequestBody.fromFile(file)))
                .onItem().transformToUni(res -> Uni.createFrom().item(ObjectStorePutResponse.builder().storage_key(putObjectRequest.key()).build()))
                .onFailure().transform(error -> {
                    String errorMessage = "Error in uploading file to S3";
                    log.error(errorMessage, error);
                    return new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
                });
    }
}
