package it.gov.pagopa.atml.mil.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {
    private UUID id;
    private String firstName;
    private String lastName;
}
