package it.gov.pagopa.receipt.pdf.service.exception;

import it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum;

/**
 * Thrown in case the requested attachment was not found
 */
public class AttachmentNotFoundException extends PdfServiceException {

    private final String attachmentName;

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message   Detail message
     */
    public AttachmentNotFoundException(AppErrorCodeEnum errorCode, String message) {
        super(errorCode, message);
        this.attachmentName = null;
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public AttachmentNotFoundException(AppErrorCodeEnum errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.attachmentName = null;
    }

    /**
     * Constructs new exception with provided error code, message and the name of the attachment that was not found
     *
     * @param errorCode         Error code
     * @param message           Detail message
     * @param attachmentName    The name of the attachment that was not found
     */
    public AttachmentNotFoundException(AppErrorCodeEnum errorCode, String message, String attachmentName) {
        super(errorCode, message);
        this.attachmentName = attachmentName;
    }

    /**
     * Constructs new exception with provided error code, message, the name of the attachment that was not found and cause
     *
     * @param errorCode         Error code
     * @param message           Detail message
     * @param cause             Exception causing the constructed one
     * @param attachmentName    The name of the attachment that was not found
     */
    public AttachmentNotFoundException(AppErrorCodeEnum errorCode, String message, String attachmentName, Throwable cause) {
        super(errorCode, message, cause);
        this.attachmentName = attachmentName;
    }

    /**
     * Return the name of the attachment that was not found
     *
     * @return the name of the attachment
     */
    public String getAttachmentName() {
        return attachmentName;
    }
}