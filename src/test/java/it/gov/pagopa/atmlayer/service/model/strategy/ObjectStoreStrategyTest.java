package it.gov.pagopa.atmlayer.service.model.strategy;

import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
public class ObjectStoreStrategyTest {

  private ObjectStoreStrategy objectStoreStrategy;
  private ObjectStoreService objectStoreService;

  @BeforeEach
  public void setup(){
    objectStoreService = Mockito.mock(ObjectStoreService.class);
    Map<ObjectStoreStrategyEnum, ObjectStoreService> selectObjectStoreByType = new HashMap<>();
    selectObjectStoreByType.put(ObjectStoreStrategyEnum.AWS_S3, objectStoreService);
    objectStoreStrategy = new ObjectStoreStrategy(selectObjectStoreByType);
  }

  @Test
  public void testGetTypeWhenEnumFoundThenReturnService() {
    ObjectStoreService result = objectStoreStrategy.getType(ObjectStoreStrategyEnum.AWS_S3);
    assertEquals(objectStoreService, result);
  }

//  @Test
//  public void testGetTypeWhenEnumNotFoundThenThrowException() {
//    Assertions.assertThrows(AtmLayerException.class, () -> {
//      objectStoreStrategy.getType(ObjectStoreStrategyEnum.fromValue(""));
//    });
//  }
}
