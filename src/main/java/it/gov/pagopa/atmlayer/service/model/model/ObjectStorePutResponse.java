package it.gov.pagopa.atmlayer.service.model.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ObjectStorePutResponse {
    String storageKey;
}
