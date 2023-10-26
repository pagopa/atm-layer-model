package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BranchDto {

    private String branchId;

    private List<String> terminalId;
}
