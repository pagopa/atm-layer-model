package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode

public class UserWithProfilesDTO {
    private String userId;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private List<ProfileDTO> profiles;
}
