package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class BranchConfigs {
    //TODO: Validatore custom
    private String branchId;

    private UUID branchDefaultTemplateId;

    private Long branchDefaultTemplateVersion;

    private List<TerminalConfigs> terminals;
}
