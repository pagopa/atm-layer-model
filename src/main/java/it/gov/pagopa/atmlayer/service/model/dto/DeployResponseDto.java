package it.gov.pagopa.atmlayer.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeployResponseDto {
    private List<LinkDto> links;
    private UUID id;
    private String name;
    private String source;
    private String deploymentTime;
    private String tenantId;
    private DeployedProcessInfoDto deployedProcessInfoDto;
}
