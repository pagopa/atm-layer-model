package it.gov.pagopa.atmlayer.service.model.validators;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
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
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setStatus(StatusEnum.CREATED);
        bpmnVersion.setFunctionType("ValidFunctionType");
        bpmnVersion.setModelVersion(1L);
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> {
            bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "ValidFunctionType").await().indefinitely();
        });
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getStatusCode());
        assertEquals(AppErrorCodeEnum.BPMN_FILE_NOT_DEPLOYED.getErrorCode(), exception.getErrorCode());
        assertEquals("One or some of the referenced BPMN files are not deployed: [BpmnVersionPK(bpmnId="+ ids.iterator().next().getBpmnId() +", modelVersion=" + ids.iterator().next().getModelVersion() + ")]", exception.getMessage());
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeMissingBpmnFiles() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        bpmnVersion.setFunctionType("MENU");
        bpmnVersion.setModelVersion(1L);
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(UUID.randomUUID(), 2L));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> {
            bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "MENU").await().indefinitely();
        });
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getStatusCode());
        assertEquals(AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST.getErrorCode(), exception.getErrorCode());
        assertEquals("One or some of the referenced BPMN files do not exists: [BpmnVersionPK(bpmnId=" + ids.iterator().next().getBpmnId() + ", modelVersion=" + ids.iterator().next().getModelVersion() + ")]", exception.getMessage());
    }

    @Test
    void testValidateExistenceStatusAndFunctionTypeNotDeployedBpmn() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setStatus(StatusEnum.CREATED); // Simulate not deployed status
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        AtmLayerException exception = assertThrows(
                AtmLayerException.class,
                () -> bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "validFunctionType").await().indefinitely()
        );
        verify(bpmnVersionService, times(1)).findByPKSet(ids);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getStatusCode());
        assertEquals(AppErrorCodeEnum.BPMN_FILE_NOT_DEPLOYED.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void testValidateExistenceStatusAndFunctionType_NotValidFunctionType() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setBpmnId(UUID.randomUUID());
        bpmnVersion.setStatus(StatusEnum.DEPLOYED);
        bpmnVersion.setFunctionType("WrongFunctionType");
        bpmnVersion.setModelVersion(1L);
        List<BpmnVersion> bpmnVersions = List.of(bpmnVersion);
        Set<BpmnVersionPK> ids = Collections.singleton(new BpmnVersionPK(bpmnVersion.getBpmnId(), bpmnVersion.getModelVersion()));
        when(bpmnVersionService.findByPKSet(ids)).thenReturn(Uni.createFrom().item(bpmnVersions));
        AtmLayerException exception = assertThrows(AtmLayerException.class, () -> {
            bpmnEntityValidator.validateExistenceStatusAndFunctionType(ids, "ValidFunctionType").await().indefinitely();
        });
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getStatusCode());
        assertEquals(AppErrorCodeEnum.BPMN_FUNCTION_TYPE_DIFFERENT_FROM_REQUESTED.getErrorCode(), exception.getErrorCode());
        assertEquals("One or some of the referenced BPMN do not have functionType ValidFunctionType: [BpmnVersionPK(bpmnId=" + ids.iterator().next().getBpmnId() + ", modelVersion=" + ids.iterator().next().getModelVersion() + ")]", exception.getMessage());
    }
}
