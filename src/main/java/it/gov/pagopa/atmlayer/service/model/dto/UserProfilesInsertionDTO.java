package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.List;

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
    @Size(min = 1)
    private List<@Range(min=1) Integer> profileIds;
}
