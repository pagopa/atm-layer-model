package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchConfigs {

    @Schema(format = "byte", maxLength = 255)
    private String branchId;

    private UUID branchDefaultTemplateId;

    @Schema(minimum="1", maximum="10000")
    private Long branchDefaultTemplateVersion;

    @Schema(maxItems = 100)
    private List<TerminalConfigs> terminals;
}
