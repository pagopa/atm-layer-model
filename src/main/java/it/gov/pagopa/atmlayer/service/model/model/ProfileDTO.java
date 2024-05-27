package it.gov.pagopa.atmlayer.service.model.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ProfileDTO {
    private String description;
    private int profileId;
    @JsonIgnore
    private Timestamp createdAt;
    private Timestamp lastUpdatedAt;
}
