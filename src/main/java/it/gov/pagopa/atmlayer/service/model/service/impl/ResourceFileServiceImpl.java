package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceFileRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
@ApplicationScoped
public class ResourceFileServiceImpl implements ResourceFileService {

    @Inject
    ResourceFileRepository resourceFileRepository;

    @Override
    @WithTransaction
    public Uni<ResourceFile> save(ResourceFile resourceFile) {
        return this.resourceFileRepository.persist(resourceFile);
    }
}
