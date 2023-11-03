package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long defaultTemplateVersion;

    private List<BranchConfigs> branchesConfigs;
}
