package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedBPMNProcessDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployedDMNDecisionDefinitionDto;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.enumeration.DeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.repository.WorkflowResourceRepository;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class WorkflowResourceServiceImplTest {
    @Mock
    WorkflowResourceRepository workflowResourceRepository;
    @Mock
    WorkflowResourceStorageServiceImpl workflowResourceStorageService;
    @Mock
    ProcessClient processClient;
    @InjectMocks
    WorkflowResourceServiceImpl workflowResourceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveContentAlreadySaved() {
        WorkflowResource workflowResource = new WorkflowResource();
        workflowResource.setSha256("sha256");
        when(workflowResourceRepository.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().item(workflowResource));
        workflowResourceService.save(workflowResource)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);
        verify(workflowResourceRepository, times(1)).findBySHA256("sha256");
        verify(workflowResourceRepository, times(0)).persist(any(WorkflowResource.class));
    }

    @Test
    void checkWorkflowResourceFileExistence() {
        UUID deployableId = UUID.randomUUID();
        UUID undeployableId = UUID.randomUUID();
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.UPDATED_BUT_NOT_DEPLOYED);
        deployableWorkflowResource.setWorkflowResourceId(deployableId);
        WorkflowResource undeployableWorkflowResource = new WorkflowResource();
        undeployableWorkflowResource.setStatus(StatusEnum.DEPLOYED);
        undeployableWorkflowResource.setWorkflowResourceId(undeployableId);
        when(workflowResourceRepository.findById(deployableId)).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        when(workflowResourceRepository.findById(undeployableId)).thenReturn(Uni.createFrom().item(undeployableWorkflowResource));
        workflowResourceService.checkWorkflowResourceFileExistenceDeployable(deployableId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(true);
        workflowResourceService.checkWorkflowResourceFileExistenceDeployable(undeployableId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(false);
        deployableWorkflowResource.setStatus(StatusEnum.DEPLOY_ERROR);
        when(workflowResourceRepository.findById(deployableId)).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        workflowResourceService.checkWorkflowResourceFileExistenceDeployable(deployableId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(true);
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        when(workflowResourceRepository.findById(deployableId)).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        workflowResourceService.checkWorkflowResourceFileExistenceDeployable(deployableId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(true);
    }

    @Test
    void setWorkflowResourceStatusFileDoesNotExist() {
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        workflowResourceService.setWorkflowResourceStatus(UUID.randomUUID(), StatusEnum.WAITING_DEPLOY)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);
        verify(workflowResourceRepository, never()).persist(any(WorkflowResource.class));
    }

    @Test
    void deployNotDeployable() {
        WorkflowResource undeployableWorkflowResource = new WorkflowResource();
        undeployableWorkflowResource.setStatus(StatusEnum.DEPLOYED);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(undeployableWorkflowResource));
        workflowResourceService.deploy(UUID.randomUUID(), Optional.of(new WorkflowResource()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Il file di risorsa aggiuntiva per processo a cui si fa riferimento non può essere rilasciato");
    }

    @Test
    void deployResourceNotFound() {
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        workflowResourceService.deploy(UUID.randomUUID(), Optional.empty())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Risorsa aggiuntiva per processo non trovata");
    }

    @Test
    void deployResourceWithNoFile() {
        UUID expectedId = UUID.randomUUID();
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        WorkflowResource waitingdWorkflowResource = new WorkflowResource();
        waitingdWorkflowResource.setStatus(StatusEnum.WAITING_DEPLOY);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(waitingdWorkflowResource));
        workflowResourceService.deploy(expectedId, Optional.of(new WorkflowResource()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Nessun file associato alla risorsa aggiuntiva per processo o nessuna chiave di archiviazione trovata: ").concat(expectedId.toString()));
    }
    @Test
    void deployResourceWithBlanckStorageKey() {
        UUID expectedId = UUID.randomUUID();
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        WorkflowResource waitingdWorkflowResource = new WorkflowResource();
        waitingdWorkflowResource.setStatus(StatusEnum.WAITING_DEPLOY);
        waitingdWorkflowResource.setResourceFile(new ResourceFile());
        waitingdWorkflowResource.getResourceFile().setStorageKey("");
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(waitingdWorkflowResource));
        workflowResourceService.deploy(expectedId, Optional.of(new WorkflowResource()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Nessun file associato alla risorsa aggiuntiva per processo o nessuna chiave di archiviazione trovata: ").concat(expectedId.toString()));
    }

    @Test
    void deployResourceWithNullStorageKey() {
        UUID expectedId = UUID.randomUUID();
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        WorkflowResource waitingdWorkflowResource = new WorkflowResource();
        waitingdWorkflowResource.setStatus(StatusEnum.WAITING_DEPLOY);
        waitingdWorkflowResource.setResourceFile(new ResourceFile());
        waitingdWorkflowResource.getResourceFile().setStorageKey(null);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(waitingdWorkflowResource));
        workflowResourceService.deploy(expectedId, Optional.of(new WorkflowResource()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Nessun file associato alla risorsa aggiuntiva per processo o nessuna chiave di archiviazione trovata: ").concat(expectedId.toString()));
    }

    @Test
    void deployResourceWithNullResourceFile() {
        UUID expectedId = UUID.randomUUID();
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        WorkflowResource waitingdWorkflowResource = new WorkflowResource();
        waitingdWorkflowResource.setStatus(StatusEnum.WAITING_DEPLOY);
        waitingdWorkflowResource.setResourceFile(null);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(waitingdWorkflowResource));
        workflowResourceService.deploy(expectedId, Optional.of(new WorkflowResource()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Nessun file associato alla risorsa aggiuntiva per processo o nessuna chiave di archiviazione trovata: ").concat(expectedId.toString()));
    }



    @Test
    void deployPresignedURLFailure() {
        UUID expectedId = UUID.randomUUID();
        ResourceFile expectedResourceFile = new ResourceFile();
        expectedResourceFile.setStorageKey("storageKey");
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        WorkflowResource waitingdWorkflowResource = new WorkflowResource();
        waitingdWorkflowResource.setStatus(StatusEnum.WAITING_DEPLOY);
        waitingdWorkflowResource.setResourceFile(expectedResourceFile);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(waitingdWorkflowResource));
        when(workflowResourceStorageService.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        workflowResourceService.deploy(expectedId, Optional.of(new WorkflowResource()))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Errore nel rilascio della risorsa aggiuntiva per processo. Impossibile generare presigned URL");
    }

    @Test
    void deployProcessFailure() throws MalformedURLException {
        UUID expectedId = UUID.randomUUID();
        ResourceFile expectedResourceFile = new ResourceFile();
        expectedResourceFile.setStorageKey("storageKey");
        WorkflowResource deployableWorkflowResource = new WorkflowResource();
        deployableWorkflowResource.setStatus(StatusEnum.CREATED);
        deployableWorkflowResource.setResourceType(DeployableResourceType.BPMN);
        WorkflowResource waitingdWorkflowResource = new WorkflowResource();
        waitingdWorkflowResource.setStatus(StatusEnum.WAITING_DEPLOY);
        waitingdWorkflowResource.setResourceFile(expectedResourceFile);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(deployableWorkflowResource));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(waitingdWorkflowResource));
        when(workflowResourceStorageService.generatePresignedUrl(any(String.class))).thenReturn(Uni.createFrom().item(new URL("http://test-presigned-url")));
        when(processClient.deploy(any(String.class), any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        workflowResourceService.deploy(expectedId, Optional.of(deployableWorkflowResource))
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Errore nel rilascio della risorsa aggiuntiva per processo. La comunicazione con Process Service non è riuscita");
    }

    @Test
    void setDeployInfoFileDoesNotExist() {
        UUID expectedId = UUID.randomUUID();
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        workflowResourceService.setDeployInfo(expectedId, new DeployResponseDto())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Uno o alcuni dei file delle risorse aggiuntive per processo a cui si fa riferimento non esistono: ").concat(expectedId.toString()));
    }

    @Test
    void setDeployInfoEmptyProcessDefinitions() {
        UUID expectedId = UUID.randomUUID();
        WorkflowResource expectedWorkflowResource = new WorkflowResource();
        Map<String, DeployedBPMNProcessDefinitionDto> deployedProcessDefinitions = new HashMap<>();
        DeployResponseDto deployResponseDto = new DeployResponseDto();
        deployResponseDto.setDeployedProcessDefinitions(deployedProcessDefinitions);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        workflowResourceService.setDeployInfo(expectedId, deployResponseDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Definizioni di processo vuote dal payload di rilascio"));
    }

    @Test
    void setDeployInfoEmptyDecisionDefinitions() {
        UUID expectedId = UUID.randomUUID();
        WorkflowResource expectedWorkflowResource = new WorkflowResource();
        Map<String, DeployedDMNDecisionDefinitionDto> deployedDecisionDefinitions = new HashMap<>();
        DeployResponseDto deployResponseDto = new DeployResponseDto();
        deployResponseDto.setDeployedDecisionDefinitions(deployedDecisionDefinitions);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        workflowResourceService.setDeployInfo(expectedId, deployResponseDto)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Definizioni decisionali vuote dal payload di rilascio"));
    }

    @Test
    void saveAndUploadFailure() {
        WorkflowResource expectedWorkflowResource = new WorkflowResource();
        when(workflowResourceRepository.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        when(workflowResourceStorageService.uploadFile(any(WorkflowResource.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        workflowResourceService.saveAndUpload(new WorkflowResource(), new File("testFile.bpmn"), "filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Impossibile salvare la risorsa aggiuntiva per processo nell'Object Store. Creazione della risorsa aggiuntiva per processo interrotta");
    }

    @Test
    void createWorkflowResourceSyncError() {
        WorkflowResource workflowResource = new WorkflowResource();
        workflowResource.setResourceType(DeployableResourceType.BPMN);
        File file = new File("src/test/resources/Test.bpmn");
        when(workflowResourceRepository.findByDefinitionKey(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(workflowResourceRepository.findBySHA256(any(String.class))).thenReturn(Uni.createFrom().nullItem());
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(workflowResource));
        when(workflowResourceStorageService.uploadFile(any(WorkflowResource.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().item(new ResourceFile()));
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        workflowResourceService.createWorkflowResource(workflowResource, file, "filename")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "Problema si sincronizzazione durante la creazione della risorsa aggiuntiva per processo");
    }

    @Test
    void deleteOK() {
        WorkflowResource expectedWorkflowResource = new WorkflowResource();
        expectedWorkflowResource.setStatus(StatusEnum.CREATED);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        when(workflowResourceRepository.deleteById(any(UUID.class))).thenReturn(Uni.createFrom().item(true));
        workflowResourceService.delete(UUID.randomUUID())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted()
                .assertItem(true);
    }

    @Test
    void deleteResourceDoesNotExist() {
        UUID expectedId = UUID.randomUUID();
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        workflowResourceService.delete(expectedId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, ("Risorsa aggiuntiva per processo con Id ").concat(expectedId.toString()).concat(" non esiste"));
    }

    @Test
    void deleteNotDeletableStatus() {
        UUID expectedId = UUID.randomUUID();
        WorkflowResource expectedWorkflowResource = new WorkflowResource();
        expectedWorkflowResource.setStatus(StatusEnum.DEPLOYED);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        workflowResourceService.delete(expectedId)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class);
    }

    @Test
    void updateResourceDoesNotExist() throws NoSuchAlgorithmException, IOException {
        File file = new File("src/test/resources/Test.bpmn");
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        workflowResourceService.update(UUID.randomUUID(), file,false)
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, "La risorsa aggiuntiva di processo indicata non esiste");
    }

    @Test
    void testRollbackOK() {
        File expectedFile = new File("src/test/resources/Test.bpmn");
        WorkflowResource expectedWorkflowResource=new WorkflowResource();
        expectedWorkflowResource.setStatus(StatusEnum.UPDATED_BUT_NOT_DEPLOYED);
        expectedWorkflowResource.setDeploymentId(UUID.randomUUID());
        expectedWorkflowResource.setResourceType(DeployableResourceType.BPMN);
        expectedWorkflowResource.setSha256("sha256");
        ResourceFile resourceFile=new ResourceFile();
        resourceFile.setStorageKey("storageKey");
        expectedWorkflowResource.setResourceFile(resourceFile);
        expectedWorkflowResource.setDefinitionKey("demo11_06");
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        when(processClient.getDeployedResource(any(String.class))).thenReturn(Uni.createFrom().item(expectedFile));
        when(workflowResourceRepository.persist(any(WorkflowResource.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        when(workflowResourceStorageService.updateFile(any(WorkflowResource.class),any(File.class))).thenReturn(Uni.createFrom().item(new ResourceFile()));
        workflowResourceService.rollback(UUID.randomUUID())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }

    @Test
    void testRollbackResourceDoesNotExist(){
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().nullItem());
        workflowResourceService.rollback(UUID.randomUUID())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"La risorsa aggiuntiva per processo a cui si fa riferimento non esiste");
    }

    @Test
    void testRollbackResourceDeployed(){
        WorkflowResource expectedWorkflowResource=new WorkflowResource();
        expectedWorkflowResource.setStatus(StatusEnum.DEPLOYED);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        workflowResourceService.rollback(UUID.randomUUID())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Impossibile ripristinare: la risorsa a cui si fa riferimento è l'ultima versione rilasciata");
    }

    @Test
    void testRollbackNeverDeployed(){
        WorkflowResource expectedWorkflowResource=new WorkflowResource();
        expectedWorkflowResource.setStatus(StatusEnum.UPDATED_BUT_NOT_DEPLOYED);
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        workflowResourceService.rollback(UUID.randomUUID())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"CamundaDefinitionId della risorsa a cui si fa riferimento è NULL: impossibile ripristinare");
    }

    @Test
    void testRollbackProcessFailure(){
        WorkflowResource expectedWorkflowResource=new WorkflowResource();
        expectedWorkflowResource.setStatus(StatusEnum.UPDATED_BUT_NOT_DEPLOYED);
        expectedWorkflowResource.setDeploymentId(UUID.randomUUID());
        when(workflowResourceRepository.findById(any(UUID.class))).thenReturn(Uni.createFrom().item(expectedWorkflowResource));
        when(processClient.getDeployedResource(any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        workflowResourceService.rollback(UUID.randomUUID())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Errore durante il recupero della risorsa aggiuntiva per processo dal Process");
    }

}