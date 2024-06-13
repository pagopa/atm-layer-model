package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.ResourceCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.model.ResourceDTO;
import it.gov.pagopa.atmlayer.service.model.model.ResourceFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.service.ResourceEntityStorageService;
import it.gov.pagopa.atmlayer.service.model.utils.FileUtilities;
import jakarta.inject.Inject;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class ResourceEntityMapper {

    @Inject
    ResourceEntityStorageService resourceEntityStorageService;

    public ResourceEntity toEntityCreation(ResourceCreationDto resourceCreationDto) throws NoSuchAlgorithmException, IOException {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setSha256(FileUtilities.calculateSha256(resourceCreationDto.getFile()));
        resourceEntity.setNoDeployableResourceType(resourceCreationDto.getResourceType());
        resourceEntity.setFileName(resourceCreationDto.getFilename());
        resourceEntity.setStorageKey(resourceEntityStorageService.calculateStorageKey(
                resourceCreationDto.getResourceType(),resourceCreationDto.getPath(),resourceCreationDto.getFilename()
        ));
        resourceEntity.setDescription(resourceCreationDto.getDescription());
        resourceEntity.setEnabled(true);
        return resourceEntity;
    }

 /*   public ResourceMultipleCreationDto fromMultipartFormDataInput(MultipartFormDataInput multipartFormDataInput) {

        List<String> filenameList = multipartFormDataInput.getValues().get("filename").stream().map(FormValue::getValue).collect(Collectors.toList());

        List<File> fileList = multipartFormDataInput.getValues().get("file").stream().map(FormValue::getFileItem).map(this::fileItemToFile).toList();
        String description = multipartFormDataInput.getValues().get("description") != null ? multipartFormDataInput.getValues().get("description").stream().map(FormValue::getValue).findFirst().orElse(null) : null;
        String path = multipartFormDataInput.getValues().get("path").stream().map(FormValue::getValue).findFirst().orElse(null);
        NoDeployableResourceType resourceType = multipartFormDataInput.getValues().get("resourceType").stream().map(FormValue::getValue).map(NoDeployableResourceType::valueOf).findFirst().orElse(null);

        ResourceMultipleCreationDto response = new ResourceMultipleCreationDto();

        response.setFilenamList(filenameList);
       // response.setFileList(fileList);
        response.setDescription(description);
        response.setResourceType(resourceType);
        response.setPath(path);

        return response;
    }*/

/*    private File fileItemToFile(FileItem fileItem) {
        try {
            // Crea un file temporaneo
            File tempFile = File.createTempFile("upload-", ".tmp");
            tempFile.deleteOnExit();

            try (InputStream inputStream = fileItem.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                IOUtils.copy(inputStream, outputStream);
            }

            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la scrittura del file", e);
        }
    }*/


/*    public List<ResourceEntity> toEntityCreationList(List<ResourceCreationDto> resourceCreationDtoList) {
        return resourceCreationDtoList.stream().map(resourceCreationDto -> {
                    try {
                        return toEntityCreation(resourceCreationDto);
                    } catch (NoSuchAlgorithmException | IOException e) {
                        //todo lanciare eccezione custom
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<ResourceCreationDto> convertToResourceCreationDtoList(ResourceMultipleCreationDtoJSON multipleDto) {
        List<ResourceCreationDto> resourceCreationDtoList = new ArrayList<>();

        if (multipleDto.getFileList().size() != multipleDto.getFilenameList().size()) {
            throw new IllegalArgumentException("File list and filename list must have the same size");
        }

        for (int i = 0; i < multipleDto.getFilenameList().size(); i++) {
            ResourceCreationDto resourceCreationDto = new ResourceCreationDto();

 //           resourceCreationDto.setFile(multipleDto.getFileList().get(i));
            resourceCreationDto.setFilename(multipleDto.getFilenameList().get(i));
            resourceCreationDto.setResourceType(multipleDto.getResourceType());
            resourceCreationDto.setPath(multipleDto.getPath());
            resourceCreationDto.setDescription(multipleDto.getDescription());

            resourceCreationDtoList.add(resourceCreationDto);
        }

        return resourceCreationDtoList;
    }*/

    public abstract ResourceDTO toDTO(ResourceEntity resourceEntity);

    public List<ResourceDTO> toDTOList(List<ResourceEntity> list) {
        return list.stream().map(this::toDTO).toList();
    }

    @Mapping(source="resourceFile.id", target="resourceFileId")
    @Mapping(source="resourceFile.resourceType", target="resourceType")
    @Mapping(source="resourceFile.storageKey", target="storageKey")
    @Mapping(source="resourceFile.fileName", target="fileName")
    @Mapping(source="resourceFile.extension", target="extension")
    @Mapping(source="resourceFile.createdAt", target="resourceFileCreatedAt")
    @Mapping(source="resourceFile.lastUpdatedAt", target="resourceFileLastUpdatedAt")
    @Mapping(source="resourceFile.createdBy", target="resourceFileCreatedBy")
    @Mapping(source="resourceFile.lastUpdatedBy", target="resourceFileLastUpdatedBy")
    @Named("toResourceFrontEndDTO")
    public abstract ResourceFrontEndDTO toFrontEndDTO(ResourceEntity resourceEntity);

    @IterableMapping(qualifiedByName = "toResourceFrontEndDTO")
    @Named("toResourceFrontEndDTOList")
    public abstract List<ResourceFrontEndDTO> toFrontEndDTOList(List<ResourceEntity> resourceEntityList);

    @Mapping(source="results", target="results", qualifiedByName = "toResourceFrontEndDTOList")
    public abstract PageInfo<ResourceFrontEndDTO> toFrontEndDTOPaged(PageInfo<ResourceEntity> pagedResource);
}
