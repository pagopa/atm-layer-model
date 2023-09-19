package it.gov.pagopa.receipt.pdf.service.model;

import it.gov.pagopa.receipt.pdf.service.enumeration.ReceiptStatusType;
import lombok.*;

/**
 * Model class for the receipt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    private String eventId;
    private String id;
    private String version;
    private EventData eventData;
    private IOMessageData ioMessageData;
    private ReceiptStatusType status;
    private ReceiptMetadata mdAttach;
    private ReceiptMetadata mdAttachPayer;
    private int numRetry;
    private ReasonError reasonErr;
}
