package it.gov.pagopa.receipt.pdf.service.producer;

import com.azure.cosmos.CosmosClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

/**
 * Producer class for {@link CosmosClientBuilder} bean
 */
@Singleton
public class CosmosClientBuilderProducer {

    @Produces
    @ApplicationScoped
    public CosmosClientBuilder cosmosClientBuilder() {
        return new CosmosClientBuilder();
    }
}