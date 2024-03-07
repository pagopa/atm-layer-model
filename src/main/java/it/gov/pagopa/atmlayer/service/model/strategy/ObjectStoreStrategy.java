package it.gov.pagopa.atmlayer.service.model.strategy;

import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.ObjectStoreService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
@ApplicationScoped
public class ObjectStoreStrategy {
    private final Map<ObjectStoreStrategyEnum, ObjectStoreService> selectObjectStoreByType;

    public ObjectStoreService getType(ObjectStoreStrategyEnum objectStoreStrategyEnum) {
        ObjectStoreService objectStoreService = selectObjectStoreByType.getOrDefault(objectStoreStrategyEnum, null);
        if (Objects.isNull(objectStoreService)) {
            throw new AtmLayerException(String.format("Object Store Service non trovato : %s", objectStoreStrategyEnum.name()), Response.Status.INTERNAL_SERVER_ERROR, "INTERNAL");
        }
        log.info("objectStoreService: {}", objectStoreService);
        return objectStoreService;
    }
}