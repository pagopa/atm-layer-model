package it.gov.pagopa.atmlayer.service.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileAllDto {
    @Schema(example = "email@domain.com", format = "byte", maxLength = 255)
    private String userId;
    private UserProfileEnum profile;
    @Schema(example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp createdAt;
    @Schema(example = "{\"date\":\"2023-11-03T14:18:36.635+00:00\"}")
    private Timestamp lastUpdatedAt;
}
