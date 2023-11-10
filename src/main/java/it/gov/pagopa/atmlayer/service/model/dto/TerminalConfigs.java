package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalConfigs {
    @NotNull
    private UUID templateId;
    @NotNull
    private Long templateVersion;

    private List<String> terminalIds;
}
