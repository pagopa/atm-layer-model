package it.gov.pagopa.atmlayer.service.model.model.mil;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;

@AllArgsConstructor
@NoArgsConstructor
public class AuthPayload {
    @JsonProperty("client_secret")
    @RestForm("client_secret")
    private String clientSecret;
    @JsonProperty("client_id")
    @RestForm("client_id")
    private String clientId;
    @JsonProperty("grant_type")
    @RestForm("grant_type")
    private String grantType;

}
