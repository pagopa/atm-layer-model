package it.gov.pagopa.atmlayer.service.model.utils;

import it.gov.pagopa.atmlayer.service.model.enumeration.FileParsingUtilityValues;
import jakarta.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@ApplicationScoped
public class FileUtils {

    public static String extractIdValue(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            Element definitionsElement = (Element) document.getElementsByTagName(FileParsingUtilityValues.TAG_NAME.getValue()).item(0);
            return definitionsElement.getAttribute(FileParsingUtilityValues.ATTRIBUTE.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
