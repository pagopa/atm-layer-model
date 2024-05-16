package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@Data
public class ProfileCreationDto {
    @NotEmpty
    private String description;
    @NotNull
    @Range(min = 1)
    private int profileId;
}

