package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class UserDTO {
    private String userId;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
}
