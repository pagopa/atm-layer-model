package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class ResourceFileRepositoryTest {

    @Test
    void testFindByStorageKey() {
        ResourceFileRepository repositoryMock = Mockito.mock(ResourceFileRepository.class);
        ResourceFile entity = new ResourceFile();
        entity.setStorageKey("1");
        List<ResourceFile> mockResult = Collections.singletonList(entity);

        Mockito.when(repositoryMock.findByStorageKey("1"))
                .thenReturn(Uni.createFrom().item(mockResult.get(0)));

        ResourceFile res = repositoryMock.findByStorageKey("1").await().indefinitely();

        assertThat(res.getStorageKey(), is(equalTo("1")));
    }

    @Test
    void testFindByResourceId() {
        ResourceFileRepository repositoryMock = Mockito.mock(ResourceFileRepository.class);

        UUID testResourceId = UUID.randomUUID();
        ResourceFile entity = new ResourceFile();
        entity.setResourceEntity(new ResourceEntity());
        entity.getResourceEntity().setResourceId(testResourceId);

        Mockito.when(repositoryMock.findByResourceId(testResourceId))
                .thenReturn(Uni.createFrom().item(entity));

        ResourceFile res = repositoryMock.findByResourceId(testResourceId).await().indefinitely();

        assertThat(res.getResourceEntity().getResourceId(), is(equalTo(testResourceId)));
    }
}
