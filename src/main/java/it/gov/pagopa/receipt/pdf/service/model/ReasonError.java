package it.gov.pagopa.receipt.pdf.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReasonError {
    private int code;
    private String message;

}
