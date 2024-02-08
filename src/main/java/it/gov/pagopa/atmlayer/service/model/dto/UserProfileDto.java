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
public class UserProfileDto {
    @Schema(example = "email@domain.com")
    private String userId;
    private UserProfileEnum profile;
    private Boolean visible;
    private Boolean editable;
    private Boolean admin;
    @Schema(example = "2024-02-07T11:38:58.445+00:00")
    private Timestamp createdAt;
    @Schema(example = "2024-02-07T11:38:58.445+00:00")
    private Timestamp lastUpdatedAt;
}
