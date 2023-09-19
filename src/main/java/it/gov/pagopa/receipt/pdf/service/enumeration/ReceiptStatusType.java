package it.gov.pagopa.receipt.pdf.service.enumeration;

/**
 * Enumeration of the receipt status
 */
public enum ReceiptStatusType {
    NOT_QUEUE_SENT, INSERTED, RETRY, GENERATED, SIGNED, FAILED, IO_NOTIFIED
}
