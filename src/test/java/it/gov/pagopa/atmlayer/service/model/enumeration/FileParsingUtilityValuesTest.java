package it.gov.pagopa.atmlayer.service.model.enumeration;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class FileParsingUtilityValuesTest {

    @Test
    void testGetTagName() {
        assertEquals("bpmn:process", FileParsingUtilityValues.TAG_NAME.getValue());
    }

    @Test
    void testGetAttribute() {
        assertEquals("id", FileParsingUtilityValues.ATTRIBUTE.getValue());
    }

}
