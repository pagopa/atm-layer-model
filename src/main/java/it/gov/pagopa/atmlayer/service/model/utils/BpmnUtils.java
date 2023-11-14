package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.dto.TerminalConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BpmnUtils {

    public static Set<BpmnVersionPK> extractBpmnUUIDFromAssociations(List<BpmnBankConfig> associations) {
        return associations.stream().map(association -> BpmnVersionPK.builder()
                .bpmnId(association.getBpmnBankConfigPK().getBpmnId())
                .modelVersion(association.getBpmnBankConfigPK().getBpmnModelVersion())
                .build()
        ).collect(Collectors.toSet());
    }

    public static List<BpmnBankConfig> getAcquirerConfigs(BpmnAssociationDto bpmnAssociationDto, String acquirerId, String functionType) {
        List<BpmnBankConfig> bpmnBankConfigs = new ArrayList<>();
        BpmnBankConfig bpmnBankConfigAcquirerDefault = new BpmnBankConfig();
        bpmnBankConfigAcquirerDefault.setBpmnBankConfigPK(new BpmnBankConfigPK(bpmnAssociationDto.getDefaultTemplateId(),
                bpmnAssociationDto.getDefaultTemplateVersion(),
                acquirerId, BankConfigUtilityValues.NULL_VALUE.getValue(), BankConfigUtilityValues.NULL_VALUE.getValue()));
        bpmnBankConfigAcquirerDefault.setFunctionType(functionType);
        bpmnBankConfigs.add(bpmnBankConfigAcquirerDefault);
        if (bpmnAssociationDto.getBranchesConfigs() != null && !bpmnAssociationDto.getBranchesConfigs().isEmpty()) {
            for (BranchConfigs branchConfig : bpmnAssociationDto.getBranchesConfigs()) {
                BpmnBankConfig bpmnBankConfigBranchDefault = new BpmnBankConfig();
                Optional<BpmnBankConfigPK> optionalBpmnBankConfigPKBranch = getBpmnBankConfigPK(bpmnAssociationDto, acquirerId, branchConfig);
                if (optionalBpmnBankConfigPKBranch.isPresent()) {
                    bpmnBankConfigBranchDefault.setFunctionType(functionType);
                    bpmnBankConfigBranchDefault.setBpmnBankConfigPK(optionalBpmnBankConfigPKBranch.get());
                    bpmnBankConfigs.add(bpmnBankConfigBranchDefault);
                }
                if (branchConfig.getTerminals() != null && !branchConfig.getTerminals().isEmpty()) {
                    for (TerminalConfigs terminalConfig : branchConfig.getTerminals()) {
                        for (String terminalId : terminalConfig.getTerminalIds()) {
                            BpmnBankConfig bpmnBankConfigTerminal = new BpmnBankConfig();
                            BpmnBankConfigPK bpmnBankConfigPKTerminal = new BpmnBankConfigPK();
                            bpmnBankConfigPKTerminal.setBpmnId(terminalConfig.getTemplateId());
                            bpmnBankConfigPKTerminal.setBpmnModelVersion(terminalConfig.getTemplateVersion());
                            bpmnBankConfigPKTerminal.setAcquirerId(acquirerId);
                            bpmnBankConfigPKTerminal.setBranchId(branchConfig.getBranchId());
                            bpmnBankConfigPKTerminal.setTerminalId(terminalId);
                            bpmnBankConfigTerminal.setFunctionType(functionType);
                            bpmnBankConfigTerminal.setBpmnBankConfigPK(bpmnBankConfigPKTerminal);
                            bpmnBankConfigs.add(bpmnBankConfigTerminal);
                        }
                    }
                }
            }
        }
        return bpmnBankConfigs;
    }

    public static Optional<BpmnBankConfigPK> getBpmnBankConfigPK(BpmnAssociationDto bpmnAssociationDto, String acquirerId, BranchConfigs branchConfig) {
        if (Objects.isNull(branchConfig.getBranchDefaultTemplateId()) || Objects.isNull(branchConfig.getBranchDefaultTemplateVersion())) {
            return Optional.empty();
        }
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(branchConfig.getBranchDefaultTemplateId());
        bpmnBankConfigPK.setBpmnModelVersion(branchConfig.getBranchDefaultTemplateVersion());
        bpmnBankConfigPK.setAcquirerId(acquirerId);
        bpmnBankConfigPK.setBranchId(branchConfig.getBranchId());
        bpmnBankConfigPK.setTerminalId(BankConfigUtilityValues.NULL_VALUE.getValue());
        return Optional.of(bpmnBankConfigPK);

    }

}
