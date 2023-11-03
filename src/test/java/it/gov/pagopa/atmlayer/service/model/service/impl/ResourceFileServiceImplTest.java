package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.InjectMock;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceFileRepository;
import it.gov.pagopa.atmlayer.service.model.service.ResourceFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ResourceFileServiceImplTest {

    @Mock
    ResourceFileRepository resourceFileRepository;

    @InjectMocks
    ResourceFileServiceImpl resourceFileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveResourceFile() {
        // Crea un oggetto ResourceFile di esempio
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setFileName("test.txt");
        resourceFile.setResourceType(ResourceTypeEnum.BPMN);

        // Configura il comportamento del mock per il metodo persist
        when(resourceFileRepository.persist(resourceFile)).thenReturn(Uni.createFrom().item(resourceFile));

        // Esegui il metodo da testare
        Uni<ResourceFile> savedResourceFile = resourceFileService.save(resourceFile);

        // Verifica che l'oggetto salvato corrisponda all'oggetto restituito
        ResourceFile result = savedResourceFile.await().indefinitely();
        assertEquals(resourceFile, result);
    }
}