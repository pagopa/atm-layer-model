package it.gov.pagopa.receipt.pdf.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IOMessageData {
    private String idMessageDebtor;
    private String idMessagePayer;
}
