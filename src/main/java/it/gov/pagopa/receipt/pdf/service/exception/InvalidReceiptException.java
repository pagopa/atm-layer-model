package it.gov.pagopa.receipt.pdf.service.exception;

import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;

/**
 * Thrown in case the retrieved receipt is not valid
 */
public class InvalidReceiptException extends PdfServiceException {

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message   Detail message
     */
    public InvalidReceiptException(AppErrorCodeEnum errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public InvalidReceiptException(AppErrorCodeEnum errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
