package it.gov.pagopa.atmlayer.service.model.utils;

import com.google.common.io.Files;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.dto.TerminalConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BpmnUtils {

    public static byte[] fileToByteArray(File file) throws IOException {
        return Files.toByteArray(file);
    }

    public static String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        byte[] array = BpmnUtils.toSha256ByteArray(file);
        return BpmnUtils.toHexString(array);
    }

    public static byte[] encodeToBase64(byte[] array) {
        return Base64.getEncoder().encode(array);
    }

    public static byte[] toSha256ByteArray(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(Files.toByteArray(file));
    }

    public static byte[] base64ToByteArray(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static String byteArrayToString(byte[] byteArray) {
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    public static Set<BpmnVersionPK> extractBpmnUUIDFromAssociations(List<BpmnBankConfig> associations) {
        return associations.stream().map(association -> BpmnVersionPK.builder()
                .bpmnId(association.getBpmnBankConfigPK().getBpmnId())
                .modelVersion(association.getBpmnBankConfigPK().getBpmnModelVersion())
                .build()
        ).collect(Collectors.toSet());
    }

    public static List<BpmnBankConfig> getAcquirerConfigs(BpmnAssociationDto bpmnAssociationDto, String acquirerId, FunctionTypeEnum functionTypeEnum) {
        List<BpmnBankConfig> bpmnBankConfigs = new ArrayList<>();
        BpmnBankConfig bpmnBankConfigAcquirerDefault = new BpmnBankConfig();
        bpmnBankConfigAcquirerDefault.setBpmnBankConfigPK(new BpmnBankConfigPK(bpmnAssociationDto.getDefaultTemplateId(),
                bpmnAssociationDto.getDefaultTemplateVersion(),
                acquirerId, BankConfigUtilityValues.NULL_VALUE.getValue(), BankConfigUtilityValues.NULL_VALUE.getValue()));
        bpmnBankConfigAcquirerDefault.setFunctionType(functionTypeEnum);
        bpmnBankConfigs.add(bpmnBankConfigAcquirerDefault);
        if (bpmnAssociationDto.getBranchesConfigs() != null && !bpmnAssociationDto.getBranchesConfigs().isEmpty()) {
            for (BranchConfigs branchConfig : bpmnAssociationDto.getBranchesConfigs()) {
                BpmnBankConfig bpmnBankConfigBranchDefault = new BpmnBankConfig();
                Optional<BpmnBankConfigPK> optionalBpmnBankConfigPKBranch = getBpmnBankConfigPK(bpmnAssociationDto, acquirerId, branchConfig);
                if (optionalBpmnBankConfigPKBranch.isPresent()) {
                    bpmnBankConfigBranchDefault.setFunctionType(functionTypeEnum);
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
                            bpmnBankConfigTerminal.setFunctionType(functionTypeEnum);
                            bpmnBankConfigTerminal.setBpmnBankConfigPK(bpmnBankConfigPKTerminal);
                            bpmnBankConfigs.add(bpmnBankConfigTerminal);
                        }
                    }
                }
            }
        }
        return bpmnBankConfigs;
    }

    private static Optional<BpmnBankConfigPK> getBpmnBankConfigPK(BpmnAssociationDto bpmnAssociationDto, String acquirerId, BranchConfigs branchConfig) {
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
