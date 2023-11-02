package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TerminalConfigs {
    @NotNull
    private UUID templateId;
    @NotNull
    private Long templateVersion;

    private List<String> terminalIds;
}
