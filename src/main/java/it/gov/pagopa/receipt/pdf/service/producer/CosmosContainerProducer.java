package it.gov.pagopa.receipt.pdf.service.producer;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Producer class for {@link CosmosContainer} bean
 */
@Singleton
public class CosmosContainerProducer {

    @ConfigProperty(name = "cosmos.db.name")
    private String databaseId;

    @ConfigProperty(name = "cosmos.container.name")
    private String containerId;

    @Inject
    private CosmosClient cosmosClient;

    @Produces
    @ApplicationScoped
    public CosmosContainer cosmosContainer() {
        return this.cosmosClient
                .getDatabase(databaseId)
                .getContainer(containerId);
    }
}
