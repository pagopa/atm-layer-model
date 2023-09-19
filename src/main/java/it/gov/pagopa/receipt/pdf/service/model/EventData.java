package it.gov.pagopa.receipt.pdf.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventData {
    private String payerFiscalCode;
    private String debtorFiscalCode;
    private String transactionCreationDate;
}
