package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ResourceFileRepositoryTest {

    @Test
    public void testFindByStorageKey() {
        // Creare un mock per il repository
        ResourceFileRepository repositoryMock = Mockito.mock(ResourceFileRepository.class);

        // Crea un'istanza di ResourceFile
        ResourceFile entity = new ResourceFile();
        entity.setStorageKey("1");
        List<ResourceFile> mockResult = Collections.singletonList(entity);

        Mockito.when(repositoryMock.findByStorageKey("1"))
                .thenReturn(Uni.createFrom().item(mockResult.get(0)));

        ResourceFile res = repositoryMock.findByStorageKey("1").await().indefinitely();

        assertThat(res.getStorageKey(), is(equalTo("1")));
    }

}
