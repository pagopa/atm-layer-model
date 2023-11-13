package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoDeployableResourceType {

    HTML("html", "application/html"),
    OTHER(null, null);

    final String extension;
    final String mimetype;
}
