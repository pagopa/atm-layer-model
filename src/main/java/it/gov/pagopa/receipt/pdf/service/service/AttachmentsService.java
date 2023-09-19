package it.gov.pagopa.receipt.pdf.service.service;

import it.gov.pagopa.receipt.pdf.service.exception.*;
import it.gov.pagopa.receipt.pdf.service.model.AttachmentsDetailsResponse;

import java.io.File;

/**
 * Interface of the service to be used to retrieve the attachments
 */
public interface AttachmentsService {

    /**
     * Retrieve the attachment detail of the receipt with the provided id, only if the fiscal code is authorized to access it
     *
     * @param thirdPartyId the id of the receipt
     * @param requestFiscalCode the fiscal code of the user that request the receipt
     * @return the details of the requested attachments
     * @throws ReceiptNotFoundException thrown if a receipt with the provided id was not found
     * @throws InvalidReceiptException thrown if the retrieved receipt is invalid
     * @throws FiscalCodeNotAuthorizedException thrown if the fiscal code is not authorized to access the requested receipt
     */
    AttachmentsDetailsResponse getAttachmentsDetails(String thirdPartyId, String requestFiscalCode) throws ReceiptNotFoundException, InvalidReceiptException, FiscalCodeNotAuthorizedException;

    /**
     * Retrieve the attachment of the receipt with the provided id using the given attachment url, only if the fiscal code is authorized to access it
     *
     * @param thirdPartyId the id of the receipt
     * @param requestFiscalCode the fiscal code of the user that request the receipt
     * @param attachmentUrl the relative url to the attachment
     * @return the File with the reference to the attachment
     * @throws ReceiptNotFoundException thrown if a receipt with the provided id was not found
     * @throws InvalidReceiptException thrown if the retrieved receipt is invalid
     * @throws FiscalCodeNotAuthorizedException thrown if the fiscal code is not authorized to access the requested attachment
     * @throws BlobStorageClientException thrown for error when retrieving the attachment from the Blob Storage
     * @throws AttachmentNotFoundException thrown if the requested attachment was not found
     */
    File getAttachment(String thirdPartyId, String requestFiscalCode, String attachmentUrl)
            throws ReceiptNotFoundException, InvalidReceiptException, FiscalCodeNotAuthorizedException, BlobStorageClientException, AttachmentNotFoundException;
}
