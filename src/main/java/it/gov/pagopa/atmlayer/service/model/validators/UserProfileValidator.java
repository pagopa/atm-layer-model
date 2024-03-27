package it.gov.pagopa.atmlayer.service.model.validators;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserProfileValidator {

    public Uni<Void> validateExistenceProfileType (int profile){
        if(UserProfileEnum.valueOf(profile) == null){
            String errorMessage = String.format("Il profilo con valore %s non esiste", profile);
            throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.NO_USER_PROFILE_FOUND_FOR_PROFILE);
        }
        return Uni.createFrom().nullItem();
    }
}
