package it.gov.pagopa.atmlayer.service.model.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResourceTypeEnum {
    BPMN("bpmn","application/bpmn"),
    HTML("html","application/html");

    String extension;
    String mimetype;
}
