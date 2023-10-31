package it.gov.pagopa.atmlayer.service.model.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    CREATED("CREATED"),
    WAITING_DEPLOY("DEPLOY_IN_PROGRESS"),
    DEPLOYED("DEPLOYED"),
    DEPLOY_ERROR("DEPLOY_ERROR");

    @JsonValue
    private String value;

    public static Set<StatusEnum> getDeletableStatuses() {
        return new HashSet<>(Arrays.asList(CREATED, DEPLOY_ERROR));
    }

    public static boolean isDeletable(StatusEnum status) {
        return getDeletableStatuses().contains(status);
    }

}
