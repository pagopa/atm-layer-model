package it.gov.pagopa.atmlayer.service.model.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum S3ResourceTypeEnum {
    BPMN("bpmn","application/bpmn"),
    DMN("dmn", "application/dmn"),
    FORM("json", "application/json"),
    HTML("html", "application/html"),
    OTHER("other", null);

    String extension;
    String mimetype;
}
