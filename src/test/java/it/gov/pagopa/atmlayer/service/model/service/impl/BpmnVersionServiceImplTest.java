package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class BpmnVersionServiceImplTest {
    @Mock
    BpmnVersionRepository bpmnVersionRepoMock;

    @Mock
    BpmnBankConfigService bpmnBankConfigServiceMock;

    @InjectMocks
    BpmnVersionServiceImpl bpmnVersionServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFindByPKSetOK() {
        Set<BpmnVersionPK> bpmnVersionPKSet = new HashSet<>();
        bpmnVersionPKSet.add(new BpmnVersionPK(UUID.randomUUID(), 1L));
        bpmnVersionPKSet.add(new BpmnVersionPK(UUID.randomUUID(), 2L));
        List<BpmnVersion> expectedBpmnVersions = new ArrayList<>();
        expectedBpmnVersions.add(new BpmnVersion());
        expectedBpmnVersions.add(new BpmnVersion());
        when(bpmnVersionRepoMock.findByIds(bpmnVersionPKSet)).thenReturn(Uni.createFrom().item(expectedBpmnVersions));
        bpmnVersionServiceImpl.findByPKSet(bpmnVersionPKSet)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(expectedBpmnVersions);
        verify(bpmnVersionRepoMock, times(1)).findByIds(bpmnVersionPKSet);
    }

    @Test
    void testFindByPKSetEmptySet() {
        Set<BpmnVersionPK> bpmnVersionPKSet = new HashSet<>();
        List<BpmnVersion> expectedBpmnVersions = new ArrayList<>();
        when(bpmnVersionRepoMock.findByIds(bpmnVersionPKSet)).thenReturn(Uni.createFrom().item(expectedBpmnVersions));
        bpmnVersionServiceImpl.findByPKSet(bpmnVersionPKSet)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(expectedBpmnVersions);
        verify(bpmnVersionRepoMock, times(1)).findByIds(bpmnVersionPKSet);
    }

    @Test
    void testSaveOK() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("testSha256");
        when(bpmnVersionRepoMock.findBySHA256("testSha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.save(bpmnVersion)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion);
        verify(bpmnVersionRepoMock, times(1)).findBySHA256("testSha256");
        verify(bpmnVersionRepoMock, times(1)).persist(bpmnVersion);
    }

    @Test
    void testSaveExistingFileException() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("testSha256");
        when(bpmnVersionRepoMock.findBySHA256("testSha256")).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.save(bpmnVersion)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);
        verify(bpmnVersionRepoMock, never()).persist(any(BpmnVersion.class));
    }

    @Test
    void testDeleteOK() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.deleteById(bpmnVersionPK)).thenReturn(Uni.createFrom().item(true));
        bpmnVersionServiceImpl.delete(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(true);
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, times(1)).deleteById(bpmnVersionPK);
    }

    @Test
    void testDeleteBpmnDoesNotExistException() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.delete(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, never()).deleteById(any(BpmnVersionPK.class));
    }

    @Test
    void testDeleteBpmnIsNotDeletableException() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.delete(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, never()).deleteById(any(BpmnVersionPK.class));
    }

    @Test
    void testFindBySHA256OK() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        when(bpmnVersionRepoMock.findBySHA256("testSha256")).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.findBySHA256("testSha256")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(Optional.of(bpmnVersion));
        verify(bpmnVersionRepoMock, times(1)).findBySHA256("testSha256");
    }

    @Test
    void testFindBySHA256BpmnDoesNotExist() {
        when(bpmnVersionRepoMock.findBySHA256("testSha256")).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.findBySHA256("testSha256")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(Optional.empty());
        verify(bpmnVersionRepoMock, times(1)).findBySHA256("testSha256");
    }

    @Test
    void testFindByDefinitionKeyWhenDefinitionKeyFound() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        when(bpmnVersionRepoMock.findByDefinitionKey("testDefinitionKey")).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.findByDefinitionKey("testDefinitionKey")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(Optional.of(bpmnVersion));
        verify(bpmnVersionRepoMock, times(1)).findByDefinitionKey("testDefinitionKey");
    }

    @Test
    void testFindByDefinitionKeyWhenDefinitionKeyNotFound() {
        when(bpmnVersionRepoMock.findByDefinitionKey("testDefinitionKey")).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.findByDefinitionKey("testDefinitionKey")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(Optional.empty());
        verify(bpmnVersionRepoMock, times(1)).findByDefinitionKey("testDefinitionKey");
    }

    @Test
    void testFindByPkOK() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.findByPk(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(Optional.of(bpmnVersion));
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
    }

    @Test
    void testFindByPkWhenBpmnVersionDoesNotExist() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.findByPk(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(Optional.empty());
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
    }

    @Test
    void testPutAssociationsWhenSuccessful() {

        String acquirerId = "testAcquirerId";
        FunctionTypeEnum functionType = FunctionTypeEnum.MENU;
        List<BpmnBankConfig> bpmnBankConfigs = new ArrayList<>();
        when(bpmnBankConfigServiceMock.deleteByAcquirerIdAndFunctionType(acquirerId, functionType)).thenReturn(Uni.createFrom().item(1L));
        when(bpmnBankConfigServiceMock.saveList(bpmnBankConfigs)).thenReturn(Uni.createFrom().voidItem());
        when(bpmnBankConfigServiceMock.findByAcquirerIdAndFunctionType(acquirerId, functionType)).thenReturn(Uni.createFrom().item(bpmnBankConfigs));
        bpmnVersionServiceImpl.putAssociations(acquirerId, functionType, bpmnBankConfigs)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnBankConfigs);
        verify(bpmnBankConfigServiceMock, times(1)).deleteByAcquirerIdAndFunctionType(acquirerId, functionType);
        verify(bpmnBankConfigServiceMock, times(1)).saveList(bpmnBankConfigs);
        verify(bpmnBankConfigServiceMock, times(1)).findByAcquirerIdAndFunctionType(acquirerId, functionType);
    }

    @Test
    void testSetBpmnVersionStatusOK() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOYED)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion);
        assertEquals(StatusEnum.DEPLOYED,bpmnVersion.getStatus());
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, times(1)).persist(bpmnVersion);
    }

    @Test
    void testSetBpmnVersionStatusBpmnVersionDoesNotExist() {
        ListObjectsResponse list=mock(ListObjectsResponse.class);
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        String expectedErrorMessage = String.format("One or some of the referenced BPMN files do not exists: %s", bpmnVersionPK);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOYED)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,expectedErrorMessage);
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, never()).persist(any(BpmnVersion.class));
    }

}