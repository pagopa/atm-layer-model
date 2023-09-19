package it.gov.pagopa.receipt.pdf.service.producer;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Producer class for {@link BlobContainerClient} bean
 */
@Singleton
public class BlobContainerClientProducer {

    @ConfigProperty(name = "blob.storage.container.name")
    private String containerName;

    @Inject
    private BlobServiceClient blobServiceClient;

    @Produces
    @ApplicationScoped
    public BlobContainerClient blobContainerClient() {
        return this.blobServiceClient.getBlobContainerClient(containerName);
    }
}
