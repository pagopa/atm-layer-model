package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class FileS3Dto {

    @Size(max = 255)
    String fileContent;
}
