package hu.sztaki.ilab.longneck.weblog.parser.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import parser.LogElementDefTypeArbitrary;
import parser.LogElementsDefinition;
import parser.LogFormat;
import parser.LogFormatDefinition;

/**
 * Reads a user defined config XML and the predefined log definition XML.
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
@Service
public class ConfigurationReader {

    /** The jaxb context to read configuration from config.xml and logdefinitions.xml. */
    private JAXBContext jaxbContext;
    /** Schema factory for configuration file validation. */
    private SchemaFactory schemaFactory;

    public ConfigurationReader() throws JAXBException {
        jaxbContext = JAXBContext.newInstance("parser");
		schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public LogFormat getLogFormat(String configPath) throws
            JAXBException, SAXException {

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Schema schema = schemaFactory.newSchema(this.getClass().getClassLoader().getResource("META-INF/longneck/parser/config.xsd"));

        unmarshaller.setSchema(schema);

        LogFormat lf = null;
        try {
            lf = (LogFormat) unmarshaller.unmarshal(new FileReader(configPath));
        } catch (FileNotFoundException ex) {
            lf = (LogFormat) unmarshaller.unmarshal(this.getClass().getClassLoader().getResource(configPath));
        }

        if (lf == null) {
            throw new RuntimeException("Log configuration cannot be loaded from " + configPath);
        }

        return lf;
    }

    /**
     * Creates a mapping from log format placeholders to names.
     *
     * @return Log Element type/name pairs (E.g: %v - virtualhost)
     */
    public static Map<String, LogElementDefTypeArbitrary> getPlaceholderToLogElementDefTypeArbitraryMap(LogFormat logFormat) {
        if (logFormat.getLogElements() == null ) return null;
        Map<String, LogElementDefTypeArbitrary> result = new HashMap<String, LogElementDefTypeArbitrary>();
        for (LogElementDefTypeArbitrary lbt : logFormat.getLogElements().getLogElement()) {
            result.put(lbt.getType(), lbt);
        }

        return result;
    }

	public LogElementsDefinition getLogElementsDefinition() throws JAXBException,
			SAXException, FileNotFoundException {

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		ClassLoader classLoader = this.getClass().getClassLoader();


		Schema schema = schemaFactory.newSchema(classLoader.getResource("META-INF/longneck/parser/logdefinition.xsd"));

		unmarshaller.setSchema(schema);

		LogFormatDefinition lfd =
			(LogFormatDefinition) unmarshaller.unmarshal(
					classLoader.getResourceAsStream("META-INF/longneck/parser/logdefinition.xml"));

		return lfd.getLogElementsDefinition();

	}

	/**
     * Creates a mapping from placeholders to log element definitions.
     *
	 * @return Log Element name/definition pairs
	 */
	public static Map<String, LogElementDefTypeArbitrary> getPlaceholderToLogElementDefinitionMap(LogElementsDefinition logElementsDefinition) {
		Map<String, LogElementDefTypeArbitrary> result = new HashMap<String, LogElementDefTypeArbitrary>();
		for (LogElementDefTypeArbitrary ldt : logElementsDefinition.getLogElementDefinition()) {
			result.put(ldt.getType(), ldt);
		}

		return result;
	}

    /**
     * Creates a mapping from default log element names to placeholders.
     *
     * @param logElementsDefinition
     * @return A map from log element names to placeholders.
     */
    public static Map<String,String> getDefaultNameToPlaceholderMap(LogElementsDefinition logElementsDefinition) {
        // Build a map from names to placeholders (eg. reverse placeholderToName map)
        Map<String, String> nameToPlaceholderMap = new HashMap<String, String>();
        for (LogElementDefTypeArbitrary ldt : logElementsDefinition.getLogElementDefinition()) {
            nameToPlaceholderMap.put(ldt.getName(), ldt.getType());
        }

        return nameToPlaceholderMap;
    }
}
