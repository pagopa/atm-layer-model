package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceMultipleCreationDtoJSON {

    @NonNull
    private List<String> fileList;
    @NonNull
    private List<String> filenameList;
    @NonNull
    private NoDeployableResourceType resourceType;

    private String path;

    private String description;
}
