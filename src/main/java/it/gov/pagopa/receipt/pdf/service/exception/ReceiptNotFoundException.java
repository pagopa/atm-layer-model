package it.gov.pagopa.receipt.pdf.service.exception;

import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;

/** Thrown in case no receipt is found in the CosmosDB container */
public class ReceiptNotFoundException extends PdfServiceException {

    private final String receiptId;

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message   Detail message
     */
    public ReceiptNotFoundException(AppErrorCodeEnum errorCode, String message) {
        super(errorCode, message);
        this.receiptId = null;
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public ReceiptNotFoundException(AppErrorCodeEnum errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.receiptId = null;
    }

    /**
     * Constructs new exception with provided error code, message and the id of the receipt that was not found
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param receiptId The id of the receipt that was not found
     */
    public ReceiptNotFoundException(AppErrorCodeEnum errorCode, String message, String receiptId) {
        super(errorCode, message);
        this.receiptId = receiptId;
    }

    /**
     * Constructs new exception with provided error code, message, the id of the receipt that was not found and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     * @param receiptId The id of the receipt that was not found
     */
    public ReceiptNotFoundException(AppErrorCodeEnum errorCode, String message, String receiptId, Throwable cause) {
        super(errorCode, message, cause);
        this.receiptId = receiptId;
    }

    /**
     * Return the id of the receipt that was not found
     *
     * @return the id of the receipt
     */
    public String getReceiptId() {
        return receiptId;
    }
}


