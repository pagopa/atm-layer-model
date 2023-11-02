package it.gov.pagopa.atmlayer.service.model.properties;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

import java.util.Optional;

@ConfigMapping(prefix = "object-store", namingStrategy = ConfigMapping.NamingStrategy.KEBAB_CASE)
@StaticInitSafe
public interface ObjectStoreProperties {
    String type();

    Bucket bucket();

    Bpmn bpmn();

    interface Bucket {
        String name();

        Optional<String> endpointOverride();

        String region();

        Optional<String> secretKey();

        Optional<String> accessKey();
    }


    interface Bpmn {
        String pathTemplate();
    }


}
