package it.gov.pagopa.receipt.pdf.service.exception;

import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;

/**
 * Thrown in case some error occur when reading the temporary file with the PDF receipt content
 */
public class ErrorHandlingPdfAttachmentFileException extends PdfServiceException {

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message   Detail message
     */
    public ErrorHandlingPdfAttachmentFileException(AppErrorCodeEnum errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public ErrorHandlingPdfAttachmentFileException(AppErrorCodeEnum errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
