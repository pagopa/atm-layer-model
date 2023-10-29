package it.gov.pagopa.atmlayer.service.model.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum functionTypeEnum {
    MENU("MENU"),
    SPONTANEOUS_PAYMENT("SPONTANEOUS_PAYMENT");

    @JsonValue
    private String value;
}
