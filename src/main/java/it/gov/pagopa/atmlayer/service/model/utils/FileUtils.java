package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.enumeration.FileParsingUtilityValues;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY;

@ApplicationScoped
public class FileUtils {

    public static String extractIdValue(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        Element definitionsElement = (Element) document.getElementsByTagName(FileParsingUtilityValues.TAG_NAME.getValue()).item(0);
        String definitionKey = definitionsElement.getAttribute(FileParsingUtilityValues.ATTRIBUTE.getValue());
            if (definitionKey.isBlank()) {
                throw new AtmLayerException("Failed to find definition key in the BPMN file", Response.Status.NOT_ACCEPTABLE, BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY);
            }
        return definitionKey;
    }
}
