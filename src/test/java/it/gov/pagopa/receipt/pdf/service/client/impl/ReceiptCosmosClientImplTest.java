package it.gov.pagopa.receipt.pdf.service.client.impl;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.util.CosmosPagedIterable;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.receipt.pdf.service.client.ReceiptCosmosClient;
import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.receipt.pdf.service.exception.ReceiptNotFoundException;
import it.gov.pagopa.receipt.pdf.service.model.Receipt;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@QuarkusTest
class ReceiptCosmosClientImplTest {

    @Inject
    private ReceiptCosmosClient sut;

    @Inject
    private CosmosContainer cosmosContainerMock;

    private static Iterator<Receipt> iteratorMock;

    @BeforeAll
    static void setUp() {
        CosmosPagedIterable<Receipt> cosmosPagedIterableMock = mock(CosmosPagedIterable.class);
        iteratorMock = mock(Iterator.class);

        CosmosContainer cosmosContainerMock = mock(CosmosContainer.class);
        doReturn(cosmosPagedIterableMock).when(cosmosContainerMock).queryItems(anyString(), any(), any());
        QuarkusMock.installMockForType(cosmosContainerMock, CosmosContainer.class);

        doReturn(iteratorMock).when(cosmosPagedIterableMock).iterator();
    }

    @SneakyThrows
    @Test
    void getReceiptDocumentSuccess() {
        Receipt receipt = new Receipt();

        doReturn(true).when(iteratorMock).hasNext();
        doReturn(receipt).when(iteratorMock).next();

        Receipt result = sut.getReceiptDocument("id");

        assertEquals(receipt, result);
    }

    @SneakyThrows
    @Test
    void getReceiptDocumentFailureReceiptNotFound() {
        doReturn(false).when(iteratorMock).hasNext();

        ReceiptNotFoundException e = assertThrows(ReceiptNotFoundException.class, () -> sut.getReceiptDocument("id"));

        assertNotNull(e);
        assertEquals(AppErrorCodeEnum.PDFS_800, e.getErrorCode());

    }
}