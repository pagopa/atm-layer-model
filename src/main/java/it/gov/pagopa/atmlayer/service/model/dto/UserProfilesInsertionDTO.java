package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class UserProfilesInsertionDTO {
    @NotBlank
    private String userId;
    @NotNull
    @Range(min=1)
    private int profileId;
}
