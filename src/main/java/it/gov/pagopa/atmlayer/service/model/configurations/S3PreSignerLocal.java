package it.gov.pagopa.atmlayer.service.model.configurations;

import io.quarkus.arc.profile.UnlessBuildProfile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@UnlessBuildProfile(anyOf = {"prod", "native"})
@Slf4j
public class S3PreSignerLocal {

    @Inject
    ObjectStoreProperties objectStoreProperties;

    private AwsCredentialsProvider getAwsCredentialProvider() {
        ObjectStoreProperties.Bucket bucketProps = objectStoreProperties.bucket();
        if (bucketProps.accessKey().isEmpty() || bucketProps.secretKey().isEmpty()) {
            throw new AtmLayerException("Nessuna credenziale AWS fornita per la configurazione locale", Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.ATMLM_500);
        }
        AwsBasicCredentials awsBasicCredentials;
        awsBasicCredentials = AwsBasicCredentials.create(bucketProps.accessKey().get(), bucketProps.secretKey().get());
        return StaticCredentialsProvider.create(awsBasicCredentials);
    }

    @Singleton
    public S3Presigner localPreSigner() {
        log.info("Loading local AWS Presigner");
        ObjectStoreProperties.Bucket bucketProps = objectStoreProperties.bucket();
        if (bucketProps.endpointOverride().isEmpty()) {
            throw new AtmLayerException("Nessun endpoint AWS fornito per la configurazione locale", Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.MISSING_AWS_ENDPOINT);
        }

        return S3Presigner.builder()
                .region(Region.of(objectStoreProperties.bucket().region()))
                .credentialsProvider(getAwsCredentialProvider())
                .endpointOverride(URI.create(bucketProps.endpointOverride().get()))
                .build();
    }

    @Singleton
    public S3AsyncClient s3AsyncClient() {
        log.info("Loading local AWS S3AsyncClient");
        ObjectStoreProperties.Bucket bucketProps = objectStoreProperties.bucket();
        if (bucketProps.endpointOverride().isEmpty()) {
            throw new AtmLayerException("Nessun endpoint AWS fornito per la configurazione locale", Response.Status.INTERNAL_SERVER_ERROR, AppErrorCodeEnum.MISSING_AWS_ENDPOINT);
        }
        return S3AsyncClient.builder()
                .region(Region.of(objectStoreProperties.bucket().region()))
                .credentialsProvider(getAwsCredentialProvider())
                .endpointOverride(URI.create(bucketProps.endpointOverride().get()))
                .build();
    }
}
