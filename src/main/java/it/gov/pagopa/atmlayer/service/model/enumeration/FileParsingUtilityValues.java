package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileParsingUtilityValues {
    TAG_NAME("bpmn:process"),
    ATTRIBUTE("id");

    private String value;
}
