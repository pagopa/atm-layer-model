package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployBPMNResponseDto;
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
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void testCheckBpmnFileExistenceOK(){
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
        bpmnVersionServiceImpl.checkBpmnFileExistence(bpmnVersionPK1)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(true);
        bpmnVersionServiceImpl.checkBpmnFileExistence(bpmnVersionPK2)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(true);
        bpmnVersionServiceImpl.checkBpmnFileExistence(bpmnVersionPK3)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(false);
        verify(bpmnVersionRepoMock, times(3)).findById(any(BpmnVersionPK.class));
    }

    @Test
    void testSaveAndUploadOK(){
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        File file=new File("testFile");
        ResourceFile resourceFile=new ResourceFile();
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class),any(File.class),any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        bpmnVersionServiceImpl.saveAndUpload(bpmnVersion,file,"filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion);
        verify(bpmnFileStorageServiceMock,times(1)).uploadFile(bpmnVersion,file,"filename");
    }

    @Test
    void testSaveAndUploadFailure(){
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        File file=new File("testFile");
        ResourceFile resourceFile=new ResourceFile();
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class),any(File.class),any(String.class))).thenThrow(new AtmLayerException("S3 File Upload - invalid filename",Response.Status.INTERNAL_SERVER_ERROR, AppErrorType.INTERNAL.name()));
        bpmnVersionServiceImpl.saveAndUpload(bpmnVersion,file,"filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"S3 File Upload - invalid filename");
        verify(bpmnFileStorageServiceMock,times(1)).uploadFile(bpmnVersion,file,"filename");
    }

    @Test
    void testCreateBPMNOK(){
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile=new ResourceFile();
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class),any(File.class),any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        when(bpmnVersionRepoMock.findByDefinitionKey(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.createBPMN(bpmnVersion,file,"filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion);
        verify(bpmnVersionRepoMock,times(1)).findBySHA256("sha256");
        verify(bpmnVersionRepoMock,times(1)).persist(bpmnVersion);
        verify(bpmnVersionRepoMock,times(1)).findByDefinitionKey("demo11_06");
        verify(bpmnVersionRepoMock,times(1)).findById(new BpmnVersionPK(bpmnVersion.getBpmnId(),1L));
        verify(bpmnFileStorageServiceMock,times(1)).uploadFile(bpmnVersion,file,"filename");
    }

    @Test
    void testCreateBPMNAlreadyCreatedException(){
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile=new ResourceFile();
        when(bpmnVersionRepoMock.findByDefinitionKey(any(String.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class),any(File.class),any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        bpmnVersionServiceImpl.createBPMN(bpmnVersion,file,"filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"A BPMN with the same definitionKey already exists");
        verify(bpmnVersionRepoMock,times(1)).findByDefinitionKey("demo11_06");
        verify(bpmnVersionRepoMock,never()).findBySHA256("sha256");
        verify(bpmnVersionRepoMock,never()).persist(bpmnVersion);
        verify(bpmnVersionRepoMock,never()).findById(new BpmnVersionPK(bpmnVersion.getBpmnId(),1L));
        verify(bpmnFileStorageServiceMock,never()).uploadFile(bpmnVersion,file,"filename");
    }

    @Test
    void testCreateBPMNsaveFailure(){
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setSha256("sha256");
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        File file = new File("src/test/resources/Test.bpmn");
        ResourceFile resourceFile=new ResourceFile();
        when(bpmnVersionRepoMock.findByDefinitionKey(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.findBySHA256("sha256")).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(bpmnVersion)).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class),any(File.class),any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        bpmnVersionServiceImpl.createBPMN(bpmnVersion,file,"filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Sync problem on bpmn creation");
        verify(bpmnVersionRepoMock,times(1)).findByDefinitionKey("demo11_06");
        verify(bpmnVersionRepoMock,times(1)).findBySHA256("sha256");
        verify(bpmnVersionRepoMock,times(1)).persist(bpmnVersion);
        verify(bpmnVersionRepoMock,times(1)).findById(new BpmnVersionPK(bpmnVersion.getBpmnId(),1L));
        verify(bpmnFileStorageServiceMock,times(1)).uploadFile(bpmnVersion,file,"filename");
    }

    @Test
    void testDeployOK() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion=new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        ResourceFile resourceFile=new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        BpmnVersion bpmnVersionUpdated=new BpmnVersion();
        bpmnVersionUpdated.setStatus(StatusEnum.WAITING_DEPLOY);
        bpmnVersionUpdated.setResourceFile(resourceFile);
        URL url=new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo=new DeployedBPMNProcessDefinitionDto();
        DeployBPMNResponseDto response=new DeployBPMNResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions=new HashMap<>();
        deployedProcessDefinitions.put("key",processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class),eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersionUpdated));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }

    @Test
    void testDeployNotDeployable() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion=new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        ResourceFile resourceFile=new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        URL url=new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo=new DeployedBPMNProcessDefinitionDto();
        DeployBPMNResponseDto response=new DeployBPMNResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions=new HashMap<>();
        deployedProcessDefinitions.put("key",processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class),eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(new BpmnVersion()));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"The referenced BPMN file can not be deployed");
        verify(bpmnVersionRepoMock,times(1)).findById(bpmnVersionPK);
        verify(bpmnFileStorageServiceMock,never()).generatePresignedUrl(any(String.class));
        verify(processClientMock,never()).deploy(any(String.class),eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock,never()).persist(any(BpmnVersion.class));
    }

    @Test
    void testDeployNoFileForKey() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion=new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        URL url=new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo=new DeployedBPMNProcessDefinitionDto();
        DeployBPMNResponseDto response=new DeployBPMNResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions=new HashMap<>();
        deployedProcessDefinitions.put("key",processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class),eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"No file associated to BPMN or no storage key found: BpmnVersionPK(bpmnId="+bpmnVersion.getBpmnId()+", modelVersion="+bpmnVersion.getModelVersion()+")");
        verify(bpmnVersionRepoMock,times(2)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock,times(1)).persist(bpmnVersion);
        verify(bpmnFileStorageServiceMock,never()).generatePresignedUrl(any(String.class));
        verify(processClientMock,never()).deploy(any(String.class),eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock,times(1)).persist(any(BpmnVersion.class));
    }

    @Test
    void testDeployURLGenerationFailure() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion=new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        ResourceFile resourceFile=new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        BpmnVersion bpmnVersionUpdated=new BpmnVersion();
        bpmnVersionUpdated.setStatus(StatusEnum.WAITING_DEPLOY);
        bpmnVersionUpdated.setResourceFile(resourceFile);
        URL url=new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo=new DeployedBPMNProcessDefinitionDto();
        DeployBPMNResponseDto response=new DeployBPMNResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions=new HashMap<>();
        deployedProcessDefinitions.put("key",processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        when(processClientMock.deploy(any(String.class),eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().item(response));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersionUpdated));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Error in BPMN deploy. Fail to generate presigned URL");
        verify(bpmnVersionRepoMock,times(3)).findById(bpmnVersionPK);
        verify(bpmnVersionRepoMock,times(2)).persist(bpmnVersion);
        verify(bpmnFileStorageServiceMock,times(1)).generatePresignedUrl(any(String.class));
        verify(processClientMock,never()).deploy(any(String.class),eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock,times(2)).persist(any(BpmnVersion.class));
        assertEquals(StatusEnum.DEPLOY_ERROR,bpmnVersion.getStatus());
    }

    @Test
    void testDeployClientFailure() throws MalformedURLException {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(UUID.randomUUID(), 1L);
        BpmnVersion bpmnVersion=new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED);
        ResourceFile resourceFile=new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storage key");
        bpmnVersion.setResourceFile(resourceFile);
        URL url=new URL("http://localhost:8081/test");
        DeployedBPMNProcessDefinitionDto processInfo=new DeployedBPMNProcessDefinitionDto();
        DeployBPMNResponseDto response=new DeployBPMNResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions=new HashMap<>();
        deployedProcessDefinitions.put("key",processInfo);
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnFileStorageServiceMock.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(url));
        when(processClientMock.deploy(any(String.class),eq(DeployableResourceType.BPMN.name()))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.deploy(bpmnVersionPK)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Error in BPMN deploy. Communication with Process Service failed");
        verify(bpmnVersionRepoMock,times(3)).findById(bpmnVersionPK);
        verify(bpmnFileStorageServiceMock,times(1)).generatePresignedUrl(any(String.class));
        verify(processClientMock,times(1)).deploy(any(String.class),eq(DeployableResourceType.BPMN.name()));
        verify(bpmnVersionRepoMock,times(2)).persist(any(BpmnVersion.class));
        assertEquals(StatusEnum.DEPLOY_ERROR,bpmnVersion.getStatus());
    }

    @Test
    void testSetDeployInfoFileNotFound(){
        BpmnVersionPK bpmnVersionPK=new BpmnVersionPK(UUID.randomUUID(),1L);
        DeployBPMNResponseDto deployBPMNResponseDto =new DeployBPMNResponseDto();
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.setDeployInfo(bpmnVersionPK, deployBPMNResponseDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"One or some of the referenced BPMN files do not exists: BpmnVersionPK(bpmnId="+bpmnVersionPK.getBpmnId()+", modelVersion="+bpmnVersionPK.getModelVersion()+")");
        verify(bpmnVersionRepoMock,times(1)).findById(bpmnVersionPK);
    }

    @Test
    void testSetDeployInfoEmptyProcessInfo(){
        BpmnVersion bpmnVersion=new BpmnVersion();
        BpmnVersionPK bpmnVersionPK=new BpmnVersionPK(UUID.randomUUID(),1L);
        DeployedBPMNProcessDefinitionDto processInfo=new DeployedBPMNProcessDefinitionDto();
        DeployBPMNResponseDto response=new DeployBPMNResponseDto();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions=new HashMap<>();
        response.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(bpmnVersionRepoMock.findById(bpmnVersionPK)).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.setDeployInfo(bpmnVersionPK,response)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Empty process infos from deploy payload");
        verify(bpmnVersionRepoMock,times(1)).findById(bpmnVersionPK);
    }

    @Test
    void testGetLatestVersionOK(){
        BpmnVersion bpmnVersion1=new BpmnVersion();
        BpmnVersion bpmnVersion2=new BpmnVersion();
        UUID uuid=UUID.randomUUID();
        List<BpmnVersion> bpmnList=new ArrayList<BpmnVersion>();
        bpmnList.add(bpmnVersion1);
        bpmnList.add(bpmnVersion2);
        when(bpmnVersionRepoMock.findByIdAndFunction(any(UUID.class),any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        bpmnVersionServiceImpl.getLatestVersion(uuid,"MENU")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnVersion1);
        verify(bpmnVersionRepoMock,times(1)).findByIdAndFunction(uuid,"MENU");
    }

    @Test
    void testGetLatestVersionFailure(){
        UUID uuid=UUID.randomUUID();
        when(bpmnVersionRepoMock.findByIdAndFunction(any(UUID.class),any(String.class))).thenReturn(Uni.createFrom().nullItem());
        bpmnVersionServiceImpl.getLatestVersion(uuid,"MENU")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"NO BPMN with given ID and functionType exists");
        verify(bpmnVersionRepoMock,times(1)).findByIdAndFunction(uuid,"MENU");
    }

    @Test
    void testUpgradeOK() throws NoSuchAlgorithmException, IOException {
        BpmnUpgradeDto bpmnUpgradeDto=new BpmnUpgradeDto();
        bpmnUpgradeDto.setUuid(UUID.randomUUID());
        bpmnUpgradeDto.setFilename("filename");
        bpmnUpgradeDto.setFile(new File("src/test/resources/Test.bpmn"));
        bpmnUpgradeDto.setFunctionType("MENU");
        BpmnVersion bpmnVersion=new BpmnVersion();
        BpmnVersion bpmnVersion2=new BpmnVersion();
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setModelVersion(1L);
        bpmnVersion.setFunctionType("MENU");
        bpmnVersion.setDefinitionKey("demo11_06");
        bpmnVersion.setSha256("sha256");
        List<BpmnVersion> bpmnList=new ArrayList<BpmnVersion>();
        bpmnList.add(bpmnVersion);
        bpmnVersion2.setModelVersion(2L);
        ResourceFile resourceFile=new ResourceFile();
        BpmnDTO bpmnDTO=new BpmnDTO();
        bpmnDTO.setDeployedFileName("deployed file");
        when(bpmnVersionRepoMock.findByIdAndFunction(any(UUID.class),any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        when(bpmnVersionMapperMock.toEntityUpgrade(any(BpmnUpgradeDto.class),any(Long.class),any(String.class))).thenReturn(bpmnVersion2);
        when(bpmnVersionRepoMock.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionRepoMock.persist(any(BpmnVersion.class))).thenReturn(Uni.createFrom().item(bpmnVersion2));
        when(bpmnFileStorageServiceMock.uploadFile(any(BpmnVersion.class),any(File.class),any(String.class))).thenReturn(Uni.createFrom().item(resourceFile));
        when(bpmnVersionMapperMock.toDTO(any(BpmnVersion.class))).thenReturn(bpmnDTO);
        when(bpmnVersionRepoMock.findById(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        bpmnVersionServiceImpl.upgrade(bpmnUpgradeDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().assertItem(bpmnDTO);
    }

    @Test
    void testUpgradeDifferentDefinitionKeys() throws NoSuchAlgorithmException, IOException {
        BpmnUpgradeDto bpmnUpgradeDto=new BpmnUpgradeDto();
        bpmnUpgradeDto.setUuid(UUID.randomUUID());
        bpmnUpgradeDto.setFilename("filename");
        bpmnUpgradeDto.setFile(new File("src/test/resources/Test.bpmn"));
        bpmnUpgradeDto.setFunctionType("MENU");
        BpmnVersion bpmnVersion=new BpmnVersion();
        bpmnVersion.setDefinitionKey("different key");
        bpmnVersion.setSha256("sha256");
        List<BpmnVersion> bpmnList=new ArrayList<BpmnVersion>();
        bpmnList.add(bpmnVersion);
        when(bpmnVersionRepoMock.findByIdAndFunction(any(UUID.class),any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        bpmnVersionServiceImpl.upgrade(bpmnUpgradeDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Definition keys differ, BPMN upgrade refused");
        verify(bpmnVersionRepoMock,times(1)).findByIdAndFunction(bpmnUpgradeDto.getUuid(),"MENU");
        verify(bpmnVersionMapperMock,never()).toEntityUpgrade(any(BpmnUpgradeDto.class),any(Long.class),any(String.class));
        verify(bpmnVersionMapperMock,never()).toDTO(any(BpmnVersion.class));
    }

    @Test
    void testUpgradeShaFailure() throws NoSuchAlgorithmException, IOException {
        BpmnUpgradeDto bpmnUpgradeDto=new BpmnUpgradeDto();
        bpmnUpgradeDto.setUuid(UUID.randomUUID());
        bpmnUpgradeDto.setFilename("filename");
        bpmnUpgradeDto.setFile(new File("src/test/resources/Test.bpmn"));
        bpmnUpgradeDto.setFunctionType("MENU");
        BpmnVersion bpmnVersion=new BpmnVersion();
        bpmnVersion.setDefinitionKey("demo11_06");
        bpmnVersion.setSha256("sha256");
        List<BpmnVersion> bpmnList=new ArrayList<BpmnVersion>();
        bpmnList.add(bpmnVersion);
        when(bpmnVersionRepoMock.findByIdAndFunction(any(UUID.class),any(String.class))).thenReturn(Uni.createFrom().item(bpmnList));
        when(bpmnVersionMapperMock.toEntityUpgrade(any(BpmnUpgradeDto.class),any(Long.class),any(String.class))).thenThrow(new RuntimeException());
        bpmnVersionServiceImpl.upgrade(bpmnUpgradeDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Generic error calculating SHA256");
        verify(bpmnVersionRepoMock,times(1)).findByIdAndFunction(bpmnUpgradeDto.getUuid(),"MENU");
        verify(bpmnVersionMapperMock,times(1)).toEntityUpgrade(any(BpmnUpgradeDto.class),any(Long.class),any(String.class));
        verify(bpmnVersionMapperMock,never()).toDTO(any(BpmnVersion.class));
    }

}