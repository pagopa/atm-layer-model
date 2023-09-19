package it.gov.pagopa.receipt.pdf.service.exception;

import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;

/**
 * Thrown in case the request do not have the fiscal code header
 */
public class InvalidFiscalCodeHeaderException extends PdfServiceException {

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message   Detail message
     */
    public InvalidFiscalCodeHeaderException(AppErrorCodeEnum errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public InvalidFiscalCodeHeaderException(AppErrorCodeEnum errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}