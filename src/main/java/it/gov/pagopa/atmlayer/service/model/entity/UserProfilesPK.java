package it.gov.pagopa.atmlayer.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserProfilesPK implements Serializable {

    @NotBlank
    @Column(name = "user_id")
    private String userId;

    @NotNull
    @Range(min = 1)
    @Column(name = "profile_id")
    private int profileId;
}
