package it.gov.pagopa.atmlayer.service.model.exception;
import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AtmLayerExceptionTest {

    @Test
    public void testExceptionWithThrowable() {
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