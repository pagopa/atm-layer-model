package it.gov.pagopa.atmlayer.service.model.strategy;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@QuarkusTest
class ObjectStoreStrategyTest {
    @Test
    void testGetTypeWhenEnumFoundThenReturnService() {
        ObjectStoreService objectStoreService = Mockito.mock(ObjectStoreService.class);
        Map<ObjectStoreStrategyEnum, ObjectStoreService> selectObjectStoreByType = new HashMap<>();
        selectObjectStoreByType.put(ObjectStoreStrategyEnum.AWS_S3, objectStoreService);
        ObjectStoreStrategy objectStoreStrategy = new ObjectStoreStrategy(selectObjectStoreByType);
        ObjectStoreService result = objectStoreStrategy.getType(ObjectStoreStrategyEnum.AWS_S3);
        assertEquals(objectStoreService, result);
    }

    @Test
    void testGetTypeWhenEnumNotFoundThenThrowException() {
        Map<ObjectStoreStrategyEnum, ObjectStoreService> selectObjectStoreByType = Mockito.mock(HashMap.class);
        ObjectStoreStrategy objectStoreStrategy = new ObjectStoreStrategy(selectObjectStoreByType);
        when(selectObjectStoreByType.getOrDefault(any(ObjectStoreStrategyEnum.class), null))
                .thenReturn(null);
        Assertions.assertThrows(AtmLayerException.class, () -> {
            objectStoreStrategy.getType(ObjectStoreStrategyEnum.AWS_S3);
        });
    }
}
