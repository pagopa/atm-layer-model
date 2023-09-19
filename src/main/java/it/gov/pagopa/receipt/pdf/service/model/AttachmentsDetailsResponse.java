package it.gov.pagopa.receipt.pdf.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Model class for the attachment details response
 */
@Getter
@Builder
@Jacksonized
public class AttachmentsDetailsResponse {

    private List<Attachment> attachments;
}
