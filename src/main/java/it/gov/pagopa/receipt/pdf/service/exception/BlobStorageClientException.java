package it.gov.pagopa.receipt.pdf.service.exception;

import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;

/**
 * Thrown in case of error when retrieving the PDF receipt from blob storage
 */
public class BlobStorageClientException extends PdfServiceException {

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message   Detail message
     */
    public BlobStorageClientException(AppErrorCodeEnum errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public BlobStorageClientException(AppErrorCodeEnum errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
