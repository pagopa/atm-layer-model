package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ObjectStoreStrategyEnum {
    AWS_S3("AWS_S3");

    private final String value;

    public static ObjectStoreStrategyEnum fromValue(String value) {
        return ObjectStoreStrategyEnum.valueOf(value);
    }
}
