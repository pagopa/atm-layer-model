package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnProcessDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class BpmnVersionMapper {
    @Mapping(ignore = true, target = "file")
    @Mapping(ignore = true, target = "filename")
    public abstract BpmnCreationDto toDtoCreation(BpmnVersion bpmnVersion);

    public BpmnVersion toEntityCreation(BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType(bpmnCreationDto.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(FileUtilities.calculateSha256(bpmnCreationDto.getFile()));
        bpmnVersion.setDeployedFileName(bpmnCreationDto.getFilename().concat(".").concat(S3ResourceTypeEnum.BPMN.getExtension()));
        bpmnVersion.setEnabled(true);
        return bpmnVersion;
    }

    public abstract BpmnDTO toDTO(BpmnVersion bpmnVersion);

    public abstract BpmnProcessDTO toProcessDTO(BpmnDTO bpmnProcessDTO);

    @Mapping(source = "resourceFile.id", target = "resourceId")
    @Mapping(source = "resourceFile.resourceType", target = "resourceType")
    @Mapping(source = "resourceFile.storageKey", target = "storageKey")
    @Mapping(source = "resourceFile.fileName", target = "fileName")
    @Mapping(source = "resourceFile.extension", target = "extension")
    @Mapping(source = "resourceFile.createdAt", target = "resourceCreatedAt")
    @Mapping(source = "resourceFile.lastUpdatedAt", target = "resourceLastUpdatedAt")
    @Mapping(source = "resourceFile.createdBy", target = "resourceCreatedBy")
    @Mapping(source = "resourceFile.lastUpdatedAt", target = "resourceLastUpdatedBy")
    @Named("toBpmnFrontEndDTO")
    public abstract BpmnFrontEndDTO toFrontEndDTO(BpmnVersion bpmnVersion);

    public List<BpmnDTO> toDTOList(List<BpmnVersion> list) {
        return list.stream().map(this::toDTO).toList();
    }

    @IterableMapping(qualifiedByName = "toBpmnFrontEndDTO")
    @Named("toBpmnFrontEndDTOList")
    public abstract List<BpmnFrontEndDTO> toFrontEndDTOList(List<BpmnVersion> list);

    @Mapping(source = "results", target = "results", qualifiedByName = "toBpmnFrontEndDTOList")
    public abstract PageInfo<BpmnFrontEndDTO> toFrontEndDTOListPaged(PageInfo<BpmnVersion> input);

    @Mapping(ignore = true, target = "enabled")
    @Mapping(target = "resourceFile.bpmn", ignore = true)
    public abstract BpmnVersion toEntity(BpmnDTO bpmnDTO);

    public BpmnVersion toEntityUpgrade(BpmnUpgradeDto bpmnUpgradeDto, Long version, String definitionKey) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType(bpmnUpgradeDto.getFunctionType());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setSha256(FileUtilities.calculateSha256(bpmnUpgradeDto.getFile()));
        bpmnVersion.setDeployedFileName(bpmnUpgradeDto.getFilename().concat(".").concat(S3ResourceTypeEnum.BPMN.getExtension()));
        bpmnVersion.setEnabled(true);
        bpmnVersion.setModelVersion(version);
        bpmnVersion.setDefinitionKey(definitionKey);
        bpmnVersion.setBpmnId(bpmnUpgradeDto.getUuid());
        return bpmnVersion;
    }
}
