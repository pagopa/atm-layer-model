package it.gov.pagopa.atmlayer.service.model.properties;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "object-store")
@StaticInitSafe
public interface ObjectStoreProperties {
    String type();

    Bucket bucket();
    Bpmn bpmn();

    interface Bucket {
        String name();
    }


    interface Bpmn {
        String pathTemplate();
    }


}
