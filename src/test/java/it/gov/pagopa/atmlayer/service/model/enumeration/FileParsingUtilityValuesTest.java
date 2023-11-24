package it.gov.pagopa.atmlayer.service.model.enumeration;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class FileParsingUtilityValuesTest {

    @Test
    public void testGetTagName() {
        assertEquals("bpmn:process", FileParsingUtilityValues.TAG_NAME.getValue());
    }

    @Test
    public void testGetAttribute() {
        assertEquals("id", FileParsingUtilityValues.ATTRIBUTE.getValue());
    }

}
