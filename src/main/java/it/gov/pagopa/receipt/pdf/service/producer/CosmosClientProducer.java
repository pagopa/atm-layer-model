package it.gov.pagopa.receipt.pdf.service.producer;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Producer class for {@link CosmosClient} bean
 */
@Singleton
public class CosmosClientProducer {

    @ConfigProperty(name = "cosmos.receipt.key")
    String azureKey;

    @ConfigProperty(name = "cosmos.endpoint")
    String serviceEndpoint;

    @Inject
    CosmosClientBuilder cosmosClientBuilder;

    @Produces
    @ApplicationScoped
    public CosmosClient cosmosClient() {
        return cosmosClientBuilder
                .endpoint(serviceEndpoint)
                .key(azureKey)
                .buildClient();
    }
}
