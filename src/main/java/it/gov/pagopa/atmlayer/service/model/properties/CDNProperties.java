package it.gov.pagopa.atmlayer.service.model.properties;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "cdn")
public interface CDNProperties {
    String baseUrl();

    String offsetPath();
}
