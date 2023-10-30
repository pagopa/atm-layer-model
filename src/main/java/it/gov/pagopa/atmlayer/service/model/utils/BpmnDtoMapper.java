package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class BpmnDtoMapper {

    public static BpmnVersion toBpmnVersion(BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType(bpmnCreationDto.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(calculateSha256(bpmnCreationDto.getFile()));
        bpmnVersion.setEnabled(true);
        return bpmnVersion;
    }

//    public static List<BpmnBankConfig> toListBpmnBankConfig(BpmnAssociationDto bpmnAssociationDto, UUID uuid, int version) {
//        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(uuid, version);
//        List<BpmnBankConfigPK> bpmnBankConfigPKS = new ArrayList<>();
//        List<AssociationKey> associationKeys = getAllAssociation(bpmnAssociationDto);
//        for (AssociationKey associationKey: associationKeys) {
//            BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
//            bpmnBankConfigPK.setBpmnId(uuid);
//            bpmnBankConfigPK.setBpmnModelVersion(version);
//            bpmnBankConfigPK.setAcquirerId(associationKey.getAcquirerId());
//            bpmnBankConfigPK.setBranchId(associationKey.getBranchId());
//            bpmnBankConfigPK.setTerminalId(associationKey.getTerminalId());
//            bpmnBankConfigPKS.add(bpmnBankConfigPK);
//        }
//    }

    public static String calculateSha256(File file) throws NoSuchAlgorithmException, IOException {
        //TODO: Controllare che il file sia un xml .bpmn
        byte[] array = BpmnUtils.toSha256ByteArray(file);
        return BpmnUtils.toHexString(array);
    }


}
