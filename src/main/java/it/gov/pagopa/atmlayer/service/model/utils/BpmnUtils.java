package it.gov.pagopa.atmlayer.service.model.utils;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.BankConfigTripletDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.dto.TerminalConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.DUPLICATE_ASSOCIATION_CONFIGS;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.ILLEGAL_CONFIGURATION_TRIPLET;
import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.NO_BPMN_FOUND_FOR_CONFIGURATION;

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
        getBranchConfig(bpmnAssociationDto, acquirerId, functionType, bpmnBankConfigs);
        if (checkBankConfigsDuplicates(bpmnBankConfigs)) {
            throw new AtmLayerException("Duplicate triplets in input body", Response.Status.BAD_REQUEST, DUPLICATE_ASSOCIATION_CONFIGS);
        }
        return bpmnBankConfigs;
    }

    private static boolean checkBankConfigsDuplicates(List<BpmnBankConfig> bpmnBankConfigs) {
        List<BankConfigTripletDto> configTriplets = new ArrayList<>();
        for (BpmnBankConfig bpmnBankConfig : bpmnBankConfigs) {
            configTriplets.add(new BankConfigTripletDto(bpmnBankConfig.getBpmnBankConfigPK().getAcquirerId(),
                    bpmnBankConfig.getBpmnBankConfigPK().getBranchId(),
                    bpmnBankConfig.getBpmnBankConfigPK().getTerminalId()));
        }
        Set<BankConfigTripletDto> duplicates = configTriplets.stream()
                .filter(triplet -> Collections.frequency(configTriplets, triplet) > 1)
                .collect(Collectors.toSet());
        return !duplicates.isEmpty();
    }

    private static void getBranchConfig(BpmnAssociationDto bpmnAssociationDto, String acquirerId, String functionType, List<BpmnBankConfig> bpmnBankConfigs) {
        if (bpmnAssociationDto.getBranchesConfigs() != null && !bpmnAssociationDto.getBranchesConfigs().isEmpty()) {
            for (BranchConfigs branchConfig : bpmnAssociationDto.getBranchesConfigs()) {
                BpmnBankConfig bpmnBankConfigBranchDefault = new BpmnBankConfig();
                Optional<BpmnBankConfigPK> optionalBpmnBankConfigPKBranch = getBpmnBankConfigPK(acquirerId, branchConfig);
                if (optionalBpmnBankConfigPKBranch.isPresent()) {
                    bpmnBankConfigBranchDefault.setFunctionType(functionType);
                    bpmnBankConfigBranchDefault.setBpmnBankConfigPK(optionalBpmnBankConfigPKBranch.get());
                    bpmnBankConfigs.add(bpmnBankConfigBranchDefault);
                }
                getTerminalConfig(acquirerId, functionType, branchConfig, bpmnBankConfigs);
            }
        }
    }

    private static void getTerminalConfig(String acquirerId, String functionType, BranchConfigs branchConfig, List<BpmnBankConfig> bpmnBankConfigs) {
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

    public static Optional<BpmnBankConfigPK> getBpmnBankConfigPK(String acquirerId, BranchConfigs branchConfig) {
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

    public static boolean isValidBankConfigTriplet(BankConfigTripletDto bankConfigTripletDto){
        return !((StringUtils.isEmpty(bankConfigTripletDto.getAcquirerId()) && (!StringUtils.isEmpty(bankConfigTripletDto.getBranchId()) || !StringUtils.isEmpty(bankConfigTripletDto.getTerminalId()))) || StringUtils.isEmpty(bankConfigTripletDto.getBranchId()) && !StringUtils.isEmpty(bankConfigTripletDto.getTerminalId()));
    }

    public static BpmnBankConfig getSingleConfig(BpmnVersionPK bpmnVersionPK, String functionType, BankConfigTripletDto bankConfigTripletDto){
        BpmnBankConfig bpmnBankConfig=new BpmnBankConfig();
        bpmnBankConfig.setBpmnBankConfigPK(new BpmnBankConfigPK(bpmnVersionPK.getBpmnId(),bpmnVersionPK.getModelVersion(),
                bankConfigTripletDto.getAcquirerId(), bankConfigTripletDto.getBranchId(), bankConfigTripletDto.getTerminalId()));
        bpmnBankConfig.setFunctionType(functionType);
        return bpmnBankConfig;
    }
}
