package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class EnumConverter {

    public static <T extends Enum<T>> S3ResourceTypeEnum convertEnum(T specificEnum) {
        try{
            return S3ResourceTypeEnum.valueOf(String.valueOf(specificEnum));
        } catch (Exception e) {
            throw new AtmLayerException("Resource Type not allowed", Response.Status.NOT_ACCEPTABLE, AppErrorCodeEnum.ATMLM_500);
        }
    }
}
