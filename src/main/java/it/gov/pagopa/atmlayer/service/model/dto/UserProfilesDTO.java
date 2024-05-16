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
public class UserProfilesDTO {
    private String userId;
    private int profileId;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
}
