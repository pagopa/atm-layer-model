package it.gov.pagopa.receipt.pdf.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptMetadata {

    private String name;
    private String url;
}
