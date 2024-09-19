package it.gov.pagopa.atmlayer.service.model.validators;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@QuarkusTest
class BpmnEntityValidatorTest {

    @Mock
    BpmnVersionService bpmnVersionService;

    @InjectMocks
    private BpmnEntityValidator bpmnEntityValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeMissingBpmnFiles() {
        BpmnVersion existingBpmnVersion = new BpmnVersion();
        UUID existingUuid = UUID.randomUUID();
        existingBpmnVersion.setBpmnId(existingUuid);
        existingBpmnVersion.setStatus(StatusEnum.DEPLOYED);
        existingBpmnVersion.setFunctionType("MENU");
        existingBpmnVersion.setModelVersion(1L);

        List<BpmnVersion> bpmnVersions = List.of(existingBpmnVersion);

        UUID missingUuid = UUID.randomUUID();
        BpmnVersionPK existingPK = new BpmnVersionPK(existingUuid, 1L);
        BpmnVersionPK missingPK = new BpmnVersionPK(missingUuid, 2L);
        Set<BpmnVersionPK> ids = Set.of(existingPK, missingPK);

        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));

        String expectedErrorMessage = String.format("Uno o alcuni dei file BPMN a cui si fa riferimento non esistono: [%s]", missingPK);
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "MENU")
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class, expectedErrorMessage);
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeOK() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        bpmnVersion.setFunctionType("MENU");
        bpmnVersion.setModelVersion(1L);
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        Uni<Void> result = bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "MENU");
        assertDoesNotThrow(() -> result.await().indefinitely());
        verify(bpmnVersionService, times(1)).findByPKSet(ids);
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeNotDeployedBpmnFiles() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        UUID uuid = UUID.randomUUID();
        bpmnVersion.setBpmnId(uuid);
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setFunctionType("ValidFunctionType");
        bpmnVersion.setModelVersion(1L);
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").subscribe().withSubscriber(UniAssertSubscriber.create()).assertFailedWith(AtmLayerException.class,"Uno o alcuni dei file BPMN a cui si fa riferimento non sono rilascati: [BpmnVersionPK(bpmnId=" + uuid + ", modelVersion=1)]");
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeNotDeployedBpmn() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED); // Simulate not deployed status
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").subscribe().withSubscriber(UniAssertSubscriber.create()).assertFailedWith(AtmLayerException.class,"Uno o alcuni dei file BPMN a cui si fa riferimento non sono rilascati: [BpmnVersionPK(bpmnId=null, modelVersion=1)]");
        verify(bpmnVersionService, times(1)).findByPKSet(ids);
    }

    @Test
    void testValidateExistenceStatusAndFunctionType_NotValidFunctionType() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        UUID uuid = UUID.randomUUID();
        bpmnVersion.setBpmnId(uuid);
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        bpmnVersion.setFunctionType("WrongFunctionType");
        bpmnVersion.setModelVersion(1L);
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").subscribe().withSubscriber(UniAssertSubscriber.create()).assertFailedWith(AtmLayerException.class,"Uno o alcuni dei file BPMN a cui si fa riferimento non hanno tipo di funzione validFunctionType: [BpmnVersionPK(bpmnId=" + uuid +", modelVersion=1)]");
    }
}
