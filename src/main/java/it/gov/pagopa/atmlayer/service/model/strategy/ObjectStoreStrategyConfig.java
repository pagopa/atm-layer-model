package it.gov.pagopa.atmlayer.service.model.strategy;


import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@AllArgsConstructor
public class ObjectStoreStrategyConfig {
    @Inject
    Instance<ObjectStoreService> notificationStrategies;

    @ApplicationScoped
    public Map<ObjectStoreStrategyEnum, ObjectStoreService> sendNotificationByType() {
        Map<ObjectStoreStrategyEnum, ObjectStoreService> serviceByType = new HashMap<>();
        notificationStrategies.forEach(objectStoreServiceStrategy -> serviceByType.put(objectStoreServiceStrategy.getType(), objectStoreServiceStrategy));
        return serviceByType;
    }
}
