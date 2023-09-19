package it.gov.pagopa.receipt.pdf.service.client.impl;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import it.gov.pagopa.receipt.pdf.service.client.ReceiptCosmosClient;
import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.receipt.pdf.service.exception.ReceiptNotFoundException;
import it.gov.pagopa.receipt.pdf.service.model.Receipt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for the CosmosDB database
 */
@ApplicationScoped
public class ReceiptCosmosClientImpl implements ReceiptCosmosClient {

    private static final String FIND_RECEIPT_QUERY = "SELECT * FROM c WHERE c.eventId = '%s'";

    private final Logger logger = LoggerFactory.getLogger(ReceiptCosmosClientImpl.class);

    @Inject
    private CosmosContainer cosmosContainer;

    /**
     * Retrieve receipt document from CosmosDB database
     *
     * @param thirdPartyId the third party id
     * @return receipt document
     * @throws ReceiptNotFoundException in case no receipt has been found with the given idEvent
     */
    public Receipt getReceiptDocument(String thirdPartyId) throws ReceiptNotFoundException {
        String query = String.format(FIND_RECEIPT_QUERY, thirdPartyId);

        //Query the container
        CosmosPagedIterable<Receipt> queryResponse = cosmosContainer
                .queryItems(query, new CosmosQueryRequestOptions(), Receipt.class);

        if (!queryResponse.iterator().hasNext()) {
            String errMsg = String.format("Receipt with id %s not found in the defined container: %s", thirdPartyId, cosmosContainer.getId());
            logger.error(errMsg);
            throw new ReceiptNotFoundException(AppErrorCodeEnum.PDFS_800, errMsg, thirdPartyId);
        }
        return queryResponse.iterator().next();
    }
}
