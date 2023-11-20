//package it.gov.pagopa.atmlayer.service.model.strategy;
//
//import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
//import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
//import jakarta.enterprise.inject.Instance;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//class ObjectStoreStrategyConfigTest {
//    @InjectMocks
//    private ObjectStoreStrategyConfig objectStoreStrategyConfig;
//
//    @Mock
//    private Instance<ObjectStoreService> notificationStrategies;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSendNotificationByType() {
//        // Configura i mock di Instance<ObjectStoreService>
//        ObjectStoreService mockService = mock(ObjectStoreService.class);
//
//        when(notificationStrategies.iterator()).thenReturn(List.of(mockService).iterator());
//
//        // Esegui il test
//        Map<ObjectStoreStrategyEnum, ObjectStoreService> result = objectStoreStrategyConfig.sendNotificationByType();
//
//        // Verifica che il metodo getType() sia chiamato per ogni servizio
//        verify(mockService, times(1)).getType();
//
//        // Verifica che il risultato contenga l'associazione corretta
//        assertEquals(mockService, result.get(mockService.getType()));
//    }
//}