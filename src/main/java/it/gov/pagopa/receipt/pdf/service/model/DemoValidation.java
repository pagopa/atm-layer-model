package it.gov.pagopa.receipt.pdf.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoValidation {
    @NotBlank(message = "must be not null or empty")
    private String inputString;

    @NotEmpty(message = "must be not null or empty")
    private List<@NotBlank(message = "must be not null or empty") String> inputStringList;

    @NotEmpty(message = "must be not null or empty")
    private Set<@NotBlank(message = "must be not null or empty") String> inputStringSet;

    @Range(min = 1, max = 10, message = "Must be between {min} and {max}")
    private int inputInt;


}
