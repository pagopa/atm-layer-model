package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchConfigs {

    private String branchId;

    private UUID branchDefaultTemplateId;

    private Long branchDefaultTemplateVersion;

    private List<TerminalConfigs> terminals;
}
