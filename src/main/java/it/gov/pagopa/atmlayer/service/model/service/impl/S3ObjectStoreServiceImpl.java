package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.model.ObjectStoreResponse;
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
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.gov.pagopa.atmlayer.service.model.utils.FileStorageS3Utils.modifyPath;

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


    public Uni<ObjectStoreResponse> uploadFile(File file, String path, S3ResourceTypeEnum fileType, String filename) {
        if (StringUtils.isBlank(filename)) {
            String errorMessage = String.format("Aggiornamento file S3: nome file %s non valido", filename);
            log.error(errorMessage);
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
        }

        if (StringUtils.isBlank(path)) {
            String errorMessage = String.format("Aggiornamento file S3: percorso %s non valido", path);
            log.error(errorMessage);
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
        }

        if (Objects.isNull(file)) {
            String errorMessage = String.format("Aggiornamento file S3: file NULL %s non valido", filename);
            log.error(errorMessage);
            throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
        }
        PutObjectRequest putObjectRequest = fileStorageS3Utils.buildPutRequest(filename, getMimetype(fileType, filename), path);
        return Uni.createFrom().future(() -> s3.putObject(putObjectRequest, AsyncRequestBody.fromFile(file)))
                .onFailure().transform(error -> {
                    String errorMessage = "Errore nel caricamento del file su S3";
                    log.error(errorMessage, error);
                    return new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
                })
                .onItem().transformToUni(res -> {
                    log.info("success uploading from s3");
                    return Uni.createFrom().item(ObjectStoreResponse.builder().storageKey(putObjectRequest.key()).build());
                });
    }

    public Uni<ObjectStoreResponse> uploadDisabledFile(String storageKey, S3ResourceTypeEnum fileType, String filename) {

        CopyObjectRequest copyObjectRequest = fileStorageS3Utils.buildCopyRequest(storageKey, modifyPath(storageKey), getMimetype(fileType, filename));
        return Uni.createFrom().future(() -> s3.copyObject(copyObjectRequest))
                .onFailure().transform(error -> {
                    String errorMessage = "Errore nel caricamento del file da disabilitare su S3";
                    log.error(errorMessage, error);
                    return new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
                })
                .onItem().transformToUni(res -> {
                    log.info("success uploading disabled file from s3");
                    return Uni.createFrom().item(ObjectStoreResponse.builder().storageKey(copyObjectRequest.destinationKey()).build());
                });
    }

    public Uni<ObjectStoreResponse> delete(ResourceEntity resourceEntity){

        DeleteObjectRequest deleteObjectRequest = fileStorageS3Utils.buildDeleteRequest(resourceEntity.getStorageKey());
        return Uni.createFrom().future(() -> s3.deleteObject(deleteObjectRequest))
                .onFailure().transform(error -> {
                    String errorMessage = "Errore nel caricamento del file da disabilitare su S3";
                    log.error(errorMessage, error);
                    return new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name());
                })
                .onItem().transformToUni(res -> {
                    log.info("success uploading disabled file from s3");
                    return Uni.createFrom().item(ObjectStoreResponse.builder().storageKey(deleteObjectRequest.key()).build());
                });

    }

    private String getMimetype(S3ResourceTypeEnum fileType, String filename) {
        try {
            return fileType.getMimetype() == null ? URLConnection.guessContentTypeFromName(filename) : fileType.getMimetype();

        } catch (Exception e) {
            throw new AtmLayerException("Errore nell'identificazione del tipo di contenuto del file", Response.Status.BAD_REQUEST, AppErrorCodeEnum.FILE_NOT_SUPPORTED);
        }
    }
}
