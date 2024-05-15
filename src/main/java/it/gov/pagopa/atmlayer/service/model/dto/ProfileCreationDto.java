package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfileCreationDto {
    @NotNull
    private String description;
    @NotNull
    private Integer profileId;
}

