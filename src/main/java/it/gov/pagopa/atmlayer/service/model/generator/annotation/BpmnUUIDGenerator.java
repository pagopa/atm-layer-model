package it.gov.pagopa.atmlayer.service.model.generator.annotation;

import it.gov.pagopa.atmlayer.service.model.generator.BpmnUUIDGeneratorImpl;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.UuidGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IdGeneratorType(BpmnUUIDGeneratorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface BpmnUUIDGenerator {
    UuidGenerator.Style style() default UuidGenerator.Style.TIME;
}