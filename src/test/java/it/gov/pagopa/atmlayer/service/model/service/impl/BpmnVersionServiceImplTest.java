package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedBPMNProcessDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnVersionMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnVersionRepository;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class BpmnVersionServiceImplTest {
    @Mock
    BpmnVersionRepository bpmnVersionRepoMock;
    @Mock
    BpmnBankConfigService bpmnBankConfigServiceMock;
    @Mock
    BpmnFileStorageServiceImpl bpmnFileStorageServiceMock;
    @Mock
    ProcessClient processClientMock;
    @Mock
    BpmnVersionMapper bpmnVersionMapperMock;
    @InjectMocks
    BpmnVersionServiceImpl bpmnVersionServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
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
        String functionType = "MENU";
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
        assertEquals(StatusEnum.DEPLOYED, bpmnVersion.getStatus());
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, times(1)).persist(bpmnVersion);
    }

    @Test
    void testSetBpmnVersionStatusBpmnVersionDoesNotExist() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        String expectedErrorMessage = String.format("La chiave BPMN a cui si fa riferimento non esiste: %s", bpmnVersionPK);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.setBpmnVersionStatus(bpmnVersionPK, StatusEnum.DEPLOYED)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, expectedErrorMessage);
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, never()).persist(any(BpmnVersion.class));
    }

    @Test
    void testMethodsBpmnDoesNotExist() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), new Random().nextLong());
        String expectedErrorMessageSetDisabled = String.format("La chiave BPMN a cui si fa riferimento non esiste: %s", bpmnVersionPK);
        bpmnVersionServiceImpl.setDisabledBpmnAttributes(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, expectedErrorMessageSetDisabled);
        String expectedErrorMessageCheckExistence = String.format("Uno o alcuni dei file BPMN a cui si fa riferimento non esistono: %s", bpmnVersionPK);
        bpmnVersionServiceImpl.checkBpmnFileExistenceDeployable(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, expectedErrorMessageCheckExistence);
    }

    @Test
    void testCheckBpmnFileExistenceOK() {
        BpmnVersionPK bpmnVersionPK1 = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersionPK bpmnVersionPK2 = new BpmnVersionPK(UUID.randomUUID(), 2L);
        BpmnVersionPK bpmnVersionPK3 = new BpmnVersionPK(UUID.randomUUID(), 3L);
        BpmnVersion bpmnVersion1 = new BpmnVersion();
        BpmnVersion bpmnVersion2 = new BpmnVersion();
        BpmnVersion bpmnVersion3 = new BpmnVersion();
        bpmnVersion1.setStatus(StatusEnum.CREATED);
        bpmnVersion2.setStatus(StatusEnum.DEPLOY_ERROR);
        bpmnVersion3.setStatus(StatusEnum.DEPLOYED);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK1)).thenReturn(Uni.createFrom().item(bpmnVersion1));
        when(bpmnVersionRepoMock.findById(bpmnVersionPK2)).thenReturn(Uni.createFrom().item(bpmnVersion2));
        when(bpmnVersionRepoMock.findById(bpmnVersionPK3)).thenReturn(Uni.createFrom().item(bpmnVersion3));
        bpmnVersionServiceImpl.checkBpmnFileExistenceDeployable(bpmnVersionPK1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(true);
        bpmnVersionServiceImpl.checkBpmnFileExistenceDeployable(bpmnVersionPK2)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(true);
        bpmnVersionServiceImpl.checkBpmnFileExistenceDeployable(bpmnVersionPK3)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(false);
        verify(bpmnVersionRepoMock, times(3)).findById(any(BpmnVersionPK.class));
    }

    @Test
    void testSaveAndUploadOK() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        File file = new File("testFile");
        ResourceFile resourceFile = new ResourceFile();
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        bpmnVersionServiceImpl.saveAndUpload(bpmnVersion, file, "filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion);
        verify(bpmnFileStorageServiceMock, times(1)).uploadFile(bpmnVersion, file, "filename");
    }

    @Test
    void testSaveAndUploadFailure() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        File file = new File("testFile");
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class), any(File.class), any(String.class))).thenThrow(new AtmLayerException("Caricamento file S3: filename non valido", Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name()));
        bpmnVersionServiceImpl.saveAndUpload(bpmnVersion, file, "filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Caricamento file S3: filename non valido");
        verify(bpmnFileStorageServiceMock, times(1)).uploadFile(bpmnVersion, file, "filename");
    }

    @Test
    void testCreateBPMNOK() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile = new ResourceFile();
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        when(bpmnVersionRepoMock.findByDefinitionKey(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.createBPMN(bpmnVersion, file, "filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion);
        verify(bpmnVersionRepoMock, times(1)).findBySHA256("sha256");
        verify(bpmnVersionRepoMock, times(1)).persist(bpmnVersion);
        verify(bpmnVersionRepoMock, times(1)).findByDefinitionKey("demo11_06");
        verify(bpmnVersionRepoMock, times(1)).findById(new BpmnVersionPK(bpmnVersion.getBpmnId(), 1L));
        verify(bpmnFileStorageServiceMock, times(1)).uploadFile(bpmnVersion, file, "filename");
    }

    @Test
    void testCreateBPMNAlreadyCreatedException() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile = new ResourceFile();
        when(bpmnVersionRepoMock.findByDefinitionKey(any(String.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        bpmnVersionServiceImpl.createBPMN(bpmnVersion, file, "filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Esiste già un BPMN con la stessa chiave di definizione");
        verify(bpmnVersionRepoMock, times(1)).findByDefinitionKey("demo11_06");
        verify(bpmnVersionRepoMock, never()).findBySHA256("sha256");
        verify(bpmnVersionRepoMock, never()).persist(bpmnVersion);
        verify(bpmnVersionRepoMock, never()).findById(new BpmnVersionPK(bpmnVersion.getBpmnId(), 1L));
        verify(bpmnFileStorageServiceMock, never()).uploadFile(bpmnVersion, file, "filename");
    }

    @Test
    void testCreateBPMNsaveFailure() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile = new ResourceFile();
        when(bpmnVersionRepoMock.findByDefinitionKey(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        bpmnVersionServiceImpl.createBPMN(bpmnVersion, file, "filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Problema di sincronizzazione durante la creazione del BPMN");
        verify(bpmnVersionRepoMock, times(1)).findByDefinitionKey("demo11_06");
        verify(bpmnVersionRepoMock, times(1)).findBySHA256("sha256");
        verify(bpmnVersionRepoMock, times(1)).persist(bpmnVersion);
        verify(bpmnVersionRepoMock, times(1)).findById(new BpmnVersionPK(bpmnVersion.getBpmnId(), 1L));
        verify(bpmnFileStorageServiceMock, times(1)).uploadFile(bpmnVersion, file, "filename");
    }

    @Test
    void testDeployOK() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        BpmnVersion bpmnVersionUpdated = new BpmnVersion();
        bpmnVersionUpdated.setStatus(StatusEnum.WAITING_DEPLOY);
        bpmnVersionUpdated.setResourceFile(resourceFile);
        URL url = new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo = new DeployedBPMNProcessDefinitionDto();
        DeployResponseDto response = new DeployResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = new HashMap<>();
        deployedProcessDefinitions.put("key", processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        response.setId(UUID.randomUUID().toString());
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class), eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersionUpdated));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }

    @Test
    void testDeployNotDeployable() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        URL url = new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo = new DeployedBPMNProcessDefinitionDto();
        DeployResponseDto response = new DeployResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = new HashMap<>();
        deployedProcessDefinitions.put("key", processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class), eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(new BpmnVersion()));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Il file BPMN di riferimento non può essere rilasciato");
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
        verify(bpmnFileStorageServiceMock, never()).generatePresignedUrl(any(String.class));
        verify(processClientMock, never()).deploy(any(String.class), eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock, never()).persist(any(BpmnVersion.class));
    }

    @Test
    void testDeployNoFileForKey() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        URL url = new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo = new DeployedBPMNProcessDefinitionDto();
        DeployResponseDto response = new DeployResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = new HashMap<>();
        deployedProcessDefinitions.put("key", processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class), eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Nessun file associato a BPMN o nessuna chiave di archiviazione trovata: BpmnVersionPK(bpmnId=" + bpmnVersion.getBpmnId() + ", modelVersion=" + bpmnVersion.getModelVersion() + ")");
        verify(bpmnVersionRepoMock, times(2)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, times(1)).persist(bpmnVersion);
        verify(bpmnFileStorageServiceMock, never()).generatePresignedUrl(any(String.class));
        verify(processClientMock, never()).deploy(any(String.class), eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock, times(1)).persist(any(BpmnVersion.class));
    }

    @Test
    void testDeployURLGenerationFailure() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        BpmnVersion bpmnVersionUpdated = new BpmnVersion();
        bpmnVersionUpdated.setStatus(StatusEnum.WAITING_DEPLOY);
        bpmnVersionUpdated.setResourceFile(resourceFile);
        DeployedBPMNProcessDefinitionDto processInfo = new DeployedBPMNProcessDefinitionDto();
        DeployResponseDto response = new DeployResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = new HashMap<>();
        deployedProcessDefinitions.put("key", processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        when(processClientMock.deploy(any(String.class), eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersionUpdated));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Errore nel rilascio del BPMN. Impossibile generare presigned URL");
        verify(bpmnVersionRepoMock, times(3)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock, times(2)).persist(bpmnVersion);
        verify(bpmnFileStorageServiceMock, times(1)).generatePresignedUrl(any(String.class));
        verify(processClientMock, never()).deploy(any(String.class), eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock, times(2)).persist(any(BpmnVersion.class));
        assertEquals(StatusEnum.DEPLOY_ERROR, bpmnVersion.getStatus());
    }

    @Test
    void testDeployClientFailure() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        URL url = new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo = new DeployedBPMNProcessDefinitionDto();
        DeployResponseDto response = new DeployResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = new HashMap<>();
        deployedProcessDefinitions.put("key", processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class), eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Errore nel rilascio del BPMN. La comunicazione con Process Service non è riuscita");
        verify(bpmnVersionRepoMock, times(3)).findById(bpmnVersionPK);
        verify(bpmnFileStorageServiceMock, times(1)).generatePresignedUrl(any(String.class));
        verify(processClientMock, times(1)).deploy(any(String.class), eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock, times(2)).persist(any(BpmnVersion.class));
        assertEquals(StatusEnum.DEPLOY_ERROR, bpmnVersion.getStatus());
    }

    @Test
    void testSetDeployInfoFileNotFound() {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        DeployResponseDto deployResponseDto = new DeployResponseDto();
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.setDeployInfo(bpmnVersionPK, deployResponseDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Uno o alcuni dei file BPMN a cui si fa riferimento non esistono: BpmnVersionPK(bpmnId=" + bpmnVersionPK.getBpmnId() + ", modelVersion=" + bpmnVersionPK.getModelVersion() + ")");
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
    }

    @Test
    void testSetDeployInfoEmptyProcessInfo() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        DeployResponseDto response = new DeployResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = new HashMap<>();
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.setDeployInfo(bpmnVersionPK, response)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Informazioni sul processo vuote dal payload di rilascio");
        verify(bpmnVersionRepoMock, times(1)).findById(bpmnVersionPK);
    }

    @Test
    void testGetLatestVersionOK() {
        BpmnVersion bpmnVersion1 = new BpmnVersion();
        BpmnVersion bpmnVersion2 = new BpmnVersion();
        UUID uuid = UUID.randomUUID();
        List<BpmnVersion> bpmnList = new ArrayList<>();
        bpmnList.add(bpmnVersion1);
        bpmnList.add(bpmnVersion2);
        when(bpmnVersionRepoMock.findAllByIdAndFunction(any(UUID.class), any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        bpmnVersionServiceImpl.getLatestVersion(uuid, "MENU")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion1);
        verify(bpmnVersionRepoMock, times(1)).findAllByIdAndFunction(uuid, "MENU");
    }

    @Test
    void testGetLatestVersionFailure() {
        UUID uuid = UUID.randomUUID();
        when(bpmnVersionRepoMock.findAllByIdAndFunction(any(UUID.class), any(String.class))).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.getLatestVersion(uuid, "MENU")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Non esiste alcun BPMN con l'Id e il tipo di funzione specificati");
        verify(bpmnVersionRepoMock, times(1)).findAllByIdAndFunction(uuid, "MENU");
    }

    @Test
    void testUpgradeOK() throws NoSuchAlgorithmException, IOException {
        BpmnUpgradeDto bpmnUpgradeDto = new BpmnUpgradeDto();
        bpmnUpgradeDto.setUuid(UUID.randomUUID());
        bpmnUpgradeDto.setFilename("filename");
        bpmnUpgradeDto.setFile(new File("src/test/resources/Test.bpmn"));
        bpmnUpgradeDto.setFunctionType("MENU");
        BpmnVersion bpmnVersion = new BpmnVersion();
        BpmnVersion bpmnVersion2 = new BpmnVersion();
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        bpmnVersion.setFunctionType("MENU");
        bpmnVersion.setDefinitionKey("demo11_06");
        bpmnVersion.setSha256("sha256");
        List<BpmnVersion> bpmnList = new ArrayList<>();
        bpmnList.add(bpmnVersion);
        bpmnVersion2.setModelVersion(2L);
        ResourceFile resourceFile = new ResourceFile();
        BpmnDTO bpmnDTO = new BpmnDTO();
        bpmnDTO.setDeployedFileName("deployed file");
        when(bpmnVersionRepoMock.findAllByIdAndFunction(any(UUID.class), any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        when(bpmnVersionMapperMock.toEntityUpgrade(any(BpmnUpgradeDto.class), any(Long.class), any(String.class))).thenReturn(bpmnVersion2);
        when(bpmnVersionRepoMock.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersion2));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        when(bpmnVersionMapperMock.toDTO(any(BpmnVersion.class))).thenReturn(bpmnDTO);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.upgrade(bpmnUpgradeDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnDTO);
    }

    @Test
    void testUpgradeDifferentDefinitionKeys() throws NoSuchAlgorithmException, IOException {
        BpmnUpgradeDto bpmnUpgradeDto = new BpmnUpgradeDto();
        bpmnUpgradeDto.setUuid(UUID.randomUUID());
        bpmnUpgradeDto.setFilename("filename");
        bpmnUpgradeDto.setFile(new File("src/test/resources/Test.bpmn"));
        bpmnUpgradeDto.setFunctionType("MENU");
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setDefinitionKey("different key");
        bpmnVersion.setSha256("sha256");
        List<BpmnVersion> bpmnList = new ArrayList<>();
        bpmnList.add(bpmnVersion);
        when(bpmnVersionRepoMock.findAllByIdAndFunction(any(UUID.class), any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        bpmnVersionServiceImpl.upgrade(bpmnUpgradeDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Le chiavi di definizione differiscono, aggiornamento BPMN rifiutato");
        verify(bpmnVersionRepoMock, times(1)).findAllByIdAndFunction(bpmnUpgradeDto.getUuid(), "MENU");
        verify(bpmnVersionMapperMock, never()).toEntityUpgrade(any(BpmnUpgradeDto.class), any(Long.class), any(String.class));
        verify(bpmnVersionMapperMock, never()).toDTO(any(BpmnVersion.class));
    }

    @Test
    void testUpgradeShaFailure() throws NoSuchAlgorithmException, IOException {
        BpmnUpgradeDto bpmnUpgradeDto = new BpmnUpgradeDto();
        bpmnUpgradeDto.setUuid(UUID.randomUUID());
        bpmnUpgradeDto.setFilename("filename");
        bpmnUpgradeDto.setFile(new File("src/test/resources/Test.bpmn"));
        bpmnUpgradeDto.setFunctionType("MENU");
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setDefinitionKey("demo11_06");
        bpmnVersion.setSha256("sha256");
        List<BpmnVersion> bpmnList = new ArrayList<>();
        bpmnList.add(bpmnVersion);
        when(bpmnVersionRepoMock.findAllByIdAndFunction(any(UUID.class), any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        when(bpmnVersionMapperMock.toEntityUpgrade(any(BpmnUpgradeDto.class), any(Long.class), any(String.class))).thenThrow(new RuntimeException());
        bpmnVersionServiceImpl.upgrade(bpmnUpgradeDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Errore generico calcolando SHA256");
        verify(bpmnVersionRepoMock, times(1)).findAllByIdAndFunction(bpmnUpgradeDto.getUuid(), "MENU");
        verify(bpmnVersionMapperMock, times(1)).toEntityUpgrade(any(BpmnUpgradeDto.class), any(Long.class), any(String.class));
        verify(bpmnVersionMapperMock, never()).toDTO(any(BpmnVersion.class));
    }
}