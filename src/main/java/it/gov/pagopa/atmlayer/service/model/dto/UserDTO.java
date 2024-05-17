package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
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
public class UserDTO {
    private String userId;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
    private List<UserProfiles> userProfiles;
}
