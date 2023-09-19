package it.gov.pagopa.receipt.pdf.service.producer;

import com.azure.storage.blob.BlobServiceClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

/**
 * Producer class for {@link BlobServiceClientBuilder} bean
 */
@Singleton
public class BlobServiceClientBuilderProducer {

    @Produces
    @ApplicationScoped
    public BlobServiceClientBuilder blobServiceClientBuilder() {
        return new BlobServiceClientBuilder();
    }
}
