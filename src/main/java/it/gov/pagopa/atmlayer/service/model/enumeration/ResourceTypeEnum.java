package it.gov.pagopa.atmlayer.service.model.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResourceTypeEnum {
    BPMN("bpmn","application/bpmn", "bpmn:process", "id"),
    HTML("html","application/html", "NULL", "NULL"),
    DMN("dmn", "application/dmn", "decision", "id");

    String extension;
    String mimetype;
    String tagName;
    String attribute;
}
