package it.gov.pagopa.atmlayer.service.model.validators;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import jakarta.ws.rs.core.Response;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").subscribe().withSubscriber(UniAssertSubscriber.create()).assertFailedWith(AtmLayerException.class,"One or some of the referenced BPMN files are not deployed: [BpmnVersionPK(bpmnId=" + uuid + ", modelVersion=1)]");
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeMissingBpmnFiles() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        UUID uuid = UUID.randomUUID();
        bpmnVersion.setBpmnId(uuid);
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        bpmnVersion.setFunctionType("MENU");
        bpmnVersion.setModelVersion(1L);
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(UUID.randomUUID(), 2L));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").subscribe().withSubscriber(UniAssertSubscriber.create()).assertFailedWith(AtmLayerException.class,"One or some of the referenced BPMN do not have functionType validFunctionType: [BpmnVersionPK(bpmnId=" + uuid +", modelVersion=1)]");
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeNotDeployedBpmn() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED); // Simulate not deployed status
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").subscribe().withSubscriber(UniAssertSubscriber.create()).assertFailedWith(AtmLayerException.class,"One or some of the referenced BPMN files are not deployed: [BpmnVersionPK(bpmnId=null, modelVersion=1)]");
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
        bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").subscribe().withSubscriber(UniAssertSubscriber.create()).assertFailedWith(AtmLayerException.class,"One or some of the referenced BPMN do not have functionType validFunctionType: [BpmnVersionPK(bpmnId=" + uuid +", modelVersion=1)]");
    }
}
