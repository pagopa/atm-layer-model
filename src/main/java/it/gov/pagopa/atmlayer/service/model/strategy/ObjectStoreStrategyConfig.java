package it.gov.pagopa.atmlayer.service.model.strategy;


import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ObjectStoreStrategyConfig {

    private final Instance<ObjectStoreService> notificationStrategies;

    @Inject
    public ObjectStoreStrategyConfig(Instance<ObjectStoreService> notificationStrategies){
        this.notificationStrategies = notificationStrategies;
    }

    @Singleton
    public Map<ObjectStoreStrategyEnum, ObjectStoreService> sendNotificationByType() {
        Map<ObjectStoreStrategyEnum, ObjectStoreService> serviceByType = new HashMap<>();
        notificationStrategies.forEach(objectStoreServiceStrategy -> serviceByType.put(objectStoreServiceStrategy.getType(), objectStoreServiceStrategy));
        return serviceByType;
    }
}
