package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BpmnAssociationDto {

    @NotNull(message = "defaultTemplateId cannot be null")
    private UUID defaultTemplateId;

    @NotNull(message = "defaultTemplateVersion cannot be null")
    @Min(value = 1, message = "defaultTemplateVersion must be higher or equal than {value}")
    @Schema(minimum = "1", maximum = "10000")
    private Long defaultTemplateVersion;

    @Schema(maxItems = 50000)
    private List<BranchConfigs> branchesConfigs;
}
