package it.gov.pagopa.atmlayer.service.model.model.mil;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MILAccessToken {
    @JsonProperty("access_token")
    public String accessToken;
    @JsonProperty("token_type")
    public String tokenType;
    @JsonProperty("expires_in")
    public int expiresIn;
}