package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum UserProfileEnum {
    GUEST(1),
    OPERATOR(2),
    ADMIN(3);

    private final int value;
    private static final Map<Integer, UserProfileEnum> map = new HashMap<>();

    static {
        for (UserProfileEnum profileEnum : UserProfileEnum.values()) {
            map.put(profileEnum.value, profileEnum);
        }
    }

    public static UserProfileEnum valueOf(int profile) {
        return map.get(profile);
    }
}
