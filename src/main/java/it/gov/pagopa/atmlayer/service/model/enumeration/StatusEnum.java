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
    UPDATED_BUT_NOT_DEPLOYED("UPDATED_BUT_NOT_DEPLOYED"),
    DEPLOYED("DEPLOYED"),
    DEPLOY_ERROR("DEPLOY_ERROR");

    @JsonValue
    private String value;

    public static Set<StatusEnum> getUpdatableAndDeletableStatuses() {
        return new HashSet<>(Arrays.asList(CREATED, DEPLOY_ERROR));
    }

    public static boolean isEditable(StatusEnum status) {
        return getUpdatableAndDeletableStatuses().contains(status);
    }

}
