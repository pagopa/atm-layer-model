package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class UserInsertionWithProfilesDTO {
    @NotBlank
    @Email(message = "must be an email address in the correct format")
    @Schema(required = true, example = "email@domain.com")
    private String userId;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotNull
    @Size(min = 1)
    private List<@Range(min=1) Integer> profileIds;
}
