package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoDeployableResourceType {
    HTML("html", "application/html", null, null);

    String extension;
    String mimetype;
    String tagName;
    String attribute;
}
