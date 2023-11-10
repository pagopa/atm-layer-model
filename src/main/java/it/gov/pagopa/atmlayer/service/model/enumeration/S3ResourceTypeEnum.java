package it.gov.pagopa.atmlayer.service.model.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum S3ResourceTypeEnum {
    BPMN("bpmn","application/bpmn", "bpmn:process", "id"),
    DMN("dmn", "application/dmn", "decision", "id"),
    FORM("json", "application/json", null, null),
    HTML("html", "application/html", null, null);

    String extension;
    String mimetype;
    String tagName;
    String attribute;
}
