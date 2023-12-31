package it.gov.pagopa.atmlayer.service.model.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UtilityValues {
    ACQUIRER_ID("acquirerId"),
    BRANCH_ID("branchId"),
    TERMINAL_ID("terminalId"),
    FUNCTION_TYPE("functionType"),
    XML_EXTENSION("xml"),
    JSON_EXTENSION("json"),
    TXT_EXTENSION("txt"),
    DISABLED_FLAG("_disabled_");

    @JsonValue
    private final String value;
}
