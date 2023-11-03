package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnVersionRepository;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class BpmnVersionServiceImplTest {
    @InjectMock
    BpmnVersionRepository bpmnVersionRepoMock;
    @InjectMocks
    BpmnVersionServiceImpl bpmnVersionServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void testSaveWhenNoExistingFileThenReturnSame() {
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setSha256("testSha256");
//        when(bpmnVersionRepoMock.findBySHA256(bpmnVersion.getSha256())).thenReturn(Uni.createFrom().item(Optional.empty()));
//        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
//        Uni<BpmnVersion> result = bpmnVersionServiceImpl.save(bpmnVersion);
//        UniAssertSubscriber<BpmnVersion> subscriber = result.subscribe().withSubscriber(UniAssertSubscriber.create());
//        subscriber.assertCompleted().assertItem(bpmnVersion);
//        verify(bpmnVersionRepoMock, times(1)).persist(any(BpmnVersion.class));
//    }
//
//    @Test
//    void testSaveWhenExistingFileThenThrowException() {
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setSha256("testSha256");
//        when(bpmnVersionRepoMock.findBySHA256(bpmnVersion.getSha256())).thenReturn(Uni.createFrom().item(Optional.of(bpmnVersion)));
//        assertThrows(AtmLayerException.class, () -> bpmnVersionServiceImpl.save(bpmnVersion).subscribe().withSubscriber(UniAssertSubscriber.create())
//                .assertFailed());
//        verify(bpmnVersionRepoMock, never()).persist(any(BpmnVersion.class));
//    }
//
//    @Test
//    void testFindByPKSetOK() {
//        Set<BpmnVersionPK> bpmnVersionPKSet = new HashSet<>();
//        bpmnVersionPKSet.add(new BpmnVersionPK(UUID.randomUUID(), 1L));
//        bpmnVersionPKSet.add(new BpmnVersionPK(UUID.randomUUID(), 2L));
//        List<BpmnVersion> expectedBpmnVersions = new ArrayList<>();
//        expectedBpmnVersions.add(new BpmnVersion());
//        expectedBpmnVersions.add(new BpmnVersion());
//        when(bpmnVersionRepoMock.findByIds(bpmnVersionPKSet)).thenReturn(Uni.createFrom().item(expectedBpmnVersions));
//        Uni<List<BpmnVersion>> result = bpmnVersionServiceImpl.findByPKSet(bpmnVersionPKSet);
//        UniAssertSubscriber<List<BpmnVersion>> subscriber = result.subscribe().withSubscriber(UniAssertSubscriber.create());
//        subscriber.assertCompleted().assertItem(expectedBpmnVersions);
//        verify(bpmnVersionRepoMock, times(1)).findByIds(bpmnVersionPKSet);
//    }
//
//    @Test
//    void testFindByPKSetEmptySet() {
//        Set<BpmnVersionPK> bpmnVersionPKSet = new HashSet<>();
//        List<BpmnVersion> expectedBpmnVersions = new ArrayList<>();
//        when(bpmnVersionRepoMock.findByIds(bpmnVersionPKSet)).thenReturn(Uni.createFrom().item(expectedBpmnVersions));
//        Uni<List<BpmnVersion>> result = bpmnVersionServiceImpl.findByPKSet(bpmnVersionPKSet);
//        UniAssertSubscriber<List<BpmnVersion>> subscriber = result.subscribe().withSubscriber(UniAssertSubscriber.create());
//        subscriber.assertCompleted().assertItem(expectedBpmnVersions);
//        verify(bpmnVersionRepoMock, times(1)).findByIds(bpmnVersionPKSet);
//    }
//
//    @Test
//    void testSaveWhenBpmnVersionDoesNotExistThenPersist() {
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setSha256("testSha256");
//        when(bpmnVersionRepoMock.findBySHA256(bpmnVersion.getSha256())).thenReturn(Uni.createFrom().item(Optional.empty()));
//        bpmnVersionServiceImpl.save(bpmnVersion).subscribe().withSubscriber(UniAssertSubscriber.create())
//                .assertCompleted();
//        verify(bpmnVersionRepoMock, times(1)).persist(any(BpmnVersion.class));
//    }
//
//    @Test
//    void testSaveWhenBpmnVersionExistsThenThrowException() {
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setSha256("testSha256");
//        when(bpmnVersionRepoMock.findBySHA256(bpmnVersion.getSha256())).thenReturn(Uni.createFrom().item(Optional.of(bpmnVersion)));
//        assertThrows(AtmLayerException.class, () -> bpmnVersionServiceImpl.save(bpmnVersion).subscribe().withSubscriber(UniAssertSubscriber.create())
//                .assertFailed());
//        verify(bpmnVersionRepoMock, never()).persist(any(BpmnVersion.class));
//    }

    @Test
    void save() {
    }

    @Test
    void delete() {
    }

    @Test
    void findBySHA256() {
    }

    @Test
    void findByDefinitionKey() {
    }

    @Test
    void findByPk() {
    }

    @Test
    void putAssociations() {
    }

    @Test
    void setBpmnVersionStatus() {
    }

    @Test
    void saveAndUpload() {
    }

    @Test
    void createBPMN() {
    }

    @Test
    void deploy() {
    }

    @Test
    void setDeployInfo() {
    }

    @Test
    void getLatestVersion() {
    }

    @Test
    void upgrade() {
    }
    // Other test methods...
}