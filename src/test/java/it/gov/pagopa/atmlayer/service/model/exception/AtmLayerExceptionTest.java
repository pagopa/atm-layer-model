package it.gov.pagopa.atmlayer.service.model.exception;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class AtmLayerExceptionTest {

    @Test
    void testExceptionWithThrowable() {
        Throwable throwable = new RuntimeException("Test error");
        AtmLayerException exception = AtmLayerException.builder().error(throwable).build();

        assertNotNull(exception);
        assertEquals("Test error", exception.getMessage());
        assertEquals(AppErrorCodeEnum.ATMLM_500.getType().name(), exception.getType());
        assertEquals(500, exception.getStatusCode());
        assertEquals(AppErrorCodeEnum.ATMLM_500.getErrorCode(), exception.getErrorCode());
        assertEquals(throwable, exception.getCause());
    }
}