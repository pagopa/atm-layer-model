package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class UserProfileCreationDto {

    @NotNull(message = "is required")
    @Email(message = "must be an email address in the correct format")
    @Schema(required = true, example = "email@domain.com", maxLength = 255)
    private String userId;
    @NotNull(message = "is required")
    @Schema(required = true, description = "1 = GUEST, 2 = OPERATOR, 3 = ADMIN", minimum = "1", maximum = "10")
    private Integer profile;
    @Schema(hidden = true)
    private Timestamp createdAt;
    @Schema(hidden = true)
    private Timestamp lastUpdatedAt;
}
