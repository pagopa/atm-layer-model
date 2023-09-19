package it.gov.pagopa.receipt.pdf.service.client.impl;

import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.DownloadRetryOptions;
import com.azure.storage.blob.options.BlobDownloadToFileOptions;
import it.gov.pagopa.receipt.pdf.service.client.ReceiptBlobClient;
import it.gov.pagopa.receipt.pdf.service.exception.AttachmentNotFoundException;
import it.gov.pagopa.receipt.pdf.service.exception.BlobStorageClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

import static it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum.*;

/**
 * Client for the Blob Storage
 */
@ApplicationScoped
public class ReceiptBlobClientImpl implements ReceiptBlobClient {

    private final Logger logger = LoggerFactory.getLogger(ReceiptBlobClientImpl.class);

    @ConfigProperty(name = "blob.storage.client.max-retry-request")
    private int maxRetryRequests;

    @ConfigProperty(name = "blob.storage.client.timeout")
    private int timeout;

    @Inject
    private BlobContainerClient blobContainerClient;

    /**
     * Retrieve a PDF receipt from the blob storage
     *
     * @param fileName file name of the PDF receipt
     * @return the file where the PDF receipt was stored
     */
    public File getAttachmentFromBlobStorage(String fileName) throws BlobStorageClientException, AttachmentNotFoundException {
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        String filePath = createTempDirectory();
        downloadAttachment(fileName, blobClient, filePath);
        return new File(filePath);
    }

    private String createTempDirectory() throws BlobStorageClientException {
        try {
            File workingDirectory = createWorkingDirectory();
            Path tempDirectory = Files.createTempDirectory(workingDirectory.toPath(), "receipt-pdf-service");
            return tempDirectory.toAbsolutePath() + "/receiptPdf.pdf";
        } catch (IOException e) {
            logger.error("Error creating the temp directory to download the PDF receipt from Blob Storage");
            throw new BlobStorageClientException(PDFS_600, PDFS_600.getErrorMessage(),  e);
        }
    }

    private void downloadAttachment(String fileName, BlobClient blobClient, String filePath) throws BlobStorageClientException, AttachmentNotFoundException {
        try {
            blobClient.downloadToFileWithResponse(
                    getBlobDownloadToFileOptions(filePath),
                    Duration.ofSeconds(timeout),
                    Context.NONE);
        } catch (UncheckedIOException e) {
            logger.error("I/O error downloading the PDF receipt from Blob Storage");
            throw new BlobStorageClientException(PDFS_601, PDFS_601.getErrorMessage(),  e);
        } catch (BlobStorageException e) {
            String errMsg;
            if (e.getStatusCode() == 404) {
                errMsg = String.format("PDF receipt with name: %s not found in Blob Storage: %s", fileName, blobClient.getAccountName());
                logger.error(errMsg);
                throw new AttachmentNotFoundException(PDFS_602, errMsg, fileName, e);
            }
            errMsg = String.format("Unable to download the PDF receipt with name: %s from Blob Storage: %s. Error message from server: %s",
                    fileName,
                    blobClient.getAccountName(),
                    e.getServiceMessage()
            );
            logger.error(errMsg);
            throw new BlobStorageClientException(PDFS_603, errMsg, e);
        }
    }

    private BlobDownloadToFileOptions getBlobDownloadToFileOptions(String filePath) {
        return new BlobDownloadToFileOptions(filePath)
                .setDownloadRetryOptions(new DownloadRetryOptions().setMaxRetryRequests(maxRetryRequests))
                .setOpenOptions(new HashSet<>(
                        Arrays.asList(
                                StandardOpenOption.CREATE_NEW,
                                StandardOpenOption.WRITE,
                                StandardOpenOption.READ
                        ))
                );
    }

    private File createWorkingDirectory() throws IOException {
        File workingDirectory = new File("temp");
        if (!workingDirectory.exists()) {
            Files.createDirectory(workingDirectory.toPath());
        }
        return workingDirectory;
    }
}
