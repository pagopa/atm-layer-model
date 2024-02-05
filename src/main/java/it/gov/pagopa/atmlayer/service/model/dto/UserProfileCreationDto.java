package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class UserProfileCreationDto {

    @NotNull(message = "is required")
    @Email(message = "must be an email address in the correct format")
    private String userId;
    @NotNull(message = "is required")
    private Integer profile;
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
}
