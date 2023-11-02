package it.gov.pagopa.atmlayer.service.model.generator;

import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.generator.annotation.BpmnUUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.uuid.UuidGenerator;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;


@Slf4j
public class BpmnUUIDGeneratorImpl extends UuidGenerator {

    public BpmnUUIDGeneratorImpl(BpmnUUIDGenerator config, Member idMember, CustomIdGeneratorCreationContext creationContext) {
        super(getUuidGeneratorAnnotation(config.style()), idMember, creationContext);
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        BpmnVersion bpmnVersion = (BpmnVersion) currentValue;
        if (bpmnVersion.getBpmnId() == null) {
            return (Serializable) super.generate(session, owner, currentValue, eventType);
        }
        return bpmnVersion.getBpmnId();
    }

    private static org.hibernate.annotations.UuidGenerator getUuidGeneratorAnnotation(org.hibernate.annotations.UuidGenerator.Style style) {
        return new org.hibernate.annotations.UuidGenerator() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return org.hibernate.annotations.UuidGenerator.class;
            }

            @Override
            public Style style() {
                return style;
            }
        };
    }
}
