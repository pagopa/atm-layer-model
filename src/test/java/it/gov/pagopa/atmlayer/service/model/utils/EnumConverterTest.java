package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import it.gov.pagopa.atmlayer.service.model.enumeration.ObjectStoreStrategyEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumConverterTest {

  @Test
  public void testValidEnumConversion() {
    NoDeployableResourceType validEnum = NoDeployableResourceType.HTML;
    S3ResourceTypeEnum expectedType = S3ResourceTypeEnum.HTML;
    S3ResourceTypeEnum convertedType = EnumConverter.convertEnum(validEnum);
    Assertions.assertEquals(expectedType, convertedType,
        "Conversion of valid enum should match expected S3ResourceTypeEnum");
  }

  @Test
  public void testInvalidEnumConversion() {
    ObjectStoreStrategyEnum invalidEnum = ObjectStoreStrategyEnum.AWS_S3;
    AtmLayerException exception = Assertions.assertThrows(AtmLayerException.class, () -> EnumConverter.convertEnum(invalidEnum));
    Assertions.assertEquals("Resource Type not allowed", exception.getMessage());
  }
}
