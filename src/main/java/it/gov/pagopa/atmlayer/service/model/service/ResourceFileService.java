package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;

public interface ResourceFileService {

    Uni<ResourceFile> save(ResourceFile resourceFile);
}