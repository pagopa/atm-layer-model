package it.gov.pagopa.atmlayer.service.model.entity;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@QuarkusTest
class BpmnVersionTest {

    @Mock
    private ResourceFile resourceFile;


    @Test
    void generateUUIDWhenBpmnIdIsNull() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setBpmnId(null);
        bpmnVersion.generateUUID();
        assertInstanceOf(UUID.class, bpmnVersion.getBpmnId());
    }

    @Test
    void checkUUIDWhenBpmnIdNotNull() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        UUID uuid = UUID.randomUUID();
        bpmnVersion.setBpmnId(uuid);
        bpmnVersion.generateUUID();
        assertEquals(bpmnVersion.getBpmnId(),uuid);
    }

    @Test
    void getFunctionType_ShouldReturnUpperCaseFunctionType() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnVersion.setFunctionType("exampleFunction");
        String result = bpmnVersion.getFunctionType();
        assertEquals("EXAMPLEFUNCTION", result);
    }
}
