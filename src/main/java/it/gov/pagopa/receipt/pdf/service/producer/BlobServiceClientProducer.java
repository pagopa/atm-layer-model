package it.gov.pagopa.receipt.pdf.service.producer;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Producer class for {@link BlobServiceClient} bean
 */
@Singleton
public class BlobServiceClientProducer {

    @ConfigProperty(name = "blob.storage.account")
    String storageAccount;

    @ConfigProperty(name = "blob.storage.connString")
    String connectionString;

    @Inject
    BlobServiceClientBuilder blobServiceClientBuilder;

    @Produces
    @ApplicationScoped
    public BlobServiceClient blobServiceClient() {
        return blobServiceClientBuilder
                .endpoint(storageAccount)
                .connectionString(connectionString)
                .buildClient();
    }
}
