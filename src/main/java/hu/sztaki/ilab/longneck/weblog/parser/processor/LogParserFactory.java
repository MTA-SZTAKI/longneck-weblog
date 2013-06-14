package hu.sztaki.ilab.longneck.weblog.parser.processor;

import hu.sztaki.ilab.longneck.weblog.parser.service.ConfigurationReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import parser.*;

/**
 * Creates a LogParser class according the user defined configuration file.
 *
 * @author Molnár Péter <molnarp@ilab.sztaki.hu>
 */
public class LogParserFactory {

    private static final Logger logger = Logger.getLogger(LogParser.class);
    /**
     * The user configuration path.
     */
    private String configPath;
    /**
     * The regular expression to match log lines with.
     */
    private StringBuilder logLineRegexp;
    /**
     * Holds attribute aliases by their position in logConfigFormat.
     */
    private Map<Integer, LogAttribute> attributeOrder = new TreeMap<Integer, LogAttribute>();
    // Configuration data structures
    /**
     * The preprocessed Apache LogFormat configuration string.
     */
    private String apacheLogFormat;
    /**
     * Maps LogFormat placeholders to log element names.
     */
    private Map<String, LogElementDefTypeArbitrary> userplaceholderToLogElementDefTypeArbitraryMap;
    /**
     * Maps log element names to element types.
     */
    private Map<String, LogElementDefTypeArbitrary> placeholderToLogElementDefinitionMap;
    /**
     * Default name to placeholder map.
     */
    private Map<String, String> defaultNameToPlaceholderMap;
    /**
     * The complex log element.
     * This element is the generally assistant / joker element, if one of the element is not represented well,
     * this elem name, regexp or enterie element replace the lake value.
     * So avoid to break process.
     */
    private ComplexLogElementDefType complexRegexType;
    /**
     * Pattern to remove Apache LogFormat placeholder modifiers.
     */
    private final static Pattern modifierPattern = Pattern.compile("\\%([<>]){0,1}[a-zA-Z]|\\%(\\!{0,1}[^\\{]*)\\{");
    /** The supposed character encoding of the logline. User can set this value in the config.xml.*/
    String userdefined_charset = null;
    /** The 2. supposed character encoding of the logline in the case when mixing encoding occur. User can set this value in the config.xml.*/
    String userdefined_charset2 = null;
    /** In case the Url parameter fields will add to the fields. */
    Boolean creteUrlParameters = null;

    public LogParserFactory() {
    }

    public void afterPropertiesSet() throws JAXBException, SAXException, IOException {
        // Create configuration reader
        ConfigurationReader configurationReader = new ConfigurationReader();

        // Read log definition defaults
        LogElementsDefinition logElementsDefinition = configurationReader.getLogElementsDefinition();
        // Read user configuration
        LogFormat logFormat = configurationReader.getLogFormat(configPath);

        // Set processed log line format
        apacheLogFormat = removePlaceholderModifiers(logFormat.getLogConfig());
        // Create mapping from placeholders to field names
        userplaceholderToLogElementDefTypeArbitraryMap = ConfigurationReader.getPlaceholderToLogElementDefTypeArbitraryMap(logFormat);
        // Create mapping from placeholders to log element definitions
        placeholderToLogElementDefinitionMap =
                ConfigurationReader.getPlaceholderToLogElementDefinitionMap(logElementsDefinition);
        // Default names to placeholders
        defaultNameToPlaceholderMap = ConfigurationReader.getDefaultNameToPlaceholderMap(logElementsDefinition);

        // This element is the generaly assistant / joker element, if one of the element is not represented well,
        // this elem name, regexp or enterie element replace the lake value.
        complexRegexType = logElementsDefinition.getComplexLogElementDefinition();

        // Get the charactersets and url parameter variable from the config.xml
        userdefined_charset = decode(logFormat.getUrlDecodingCharset());

        userdefined_charset2 = decode(logFormat.getUrlDecodingCharset2());

        creteUrlParameters = logFormat.isCreateUrlParameters();

        // Initialize regular expression builder
        logLineRegexp = new StringBuilder(apacheLogFormat);

        // Add user element to base elem defination or rename some elem representation name.
        if (userplaceholderToLogElementDefTypeArbitraryMap != null) {
            mergeLogElementDefinitionwithUserDefination();
        }
        // Updates post-processor field lists with user overridden field names.
        updatePostProcessorFieldLists();
        // Run configuration processors
        processComplexConfigElements();
        processSimpleConfigElements();
    }

    /**
     * Add user element to base element definition or rename some element representation name.
     */
    private void mergeLogElementDefinitionwithUserDefination() {
        for (Map.Entry<String, LogElementDefTypeArbitrary> entry : userplaceholderToLogElementDefTypeArbitraryMap.entrySet()) {
            LogElementDefTypeArbitrary def = placeholderToLogElementDefinitionMap.get(entry.getKey());
            defaultNameToPlaceholderMap.put(entry.getValue().getName(), entry.getKey());
            if (def == null) {
                if (entry.getValue().getRegex() == null) {
                    entry.getValue().setRegex(complexRegexType.getRegex());
                }
                placeholderToLogElementDefinitionMap.put(entry.getKey(), entry.getValue());
                continue;
            }
            def.setName(entry.getValue().getName());
            if (entry.getValue().getRegex() != null) {
                def.setRegex(entry.getValue().getRegex());
            }
            if (entry.getValue().getPostprocessors() != null) {
                def.setPostprocessors(entry.getValue().getPostprocessors());
            }
        }
    }

    /**
     * Updates post-processor field lists with user overridden field names.
     */
    private void updatePostProcessorFieldLists() {
        for (LogElementDefTypeArbitrary def : placeholderToLogElementDefinitionMap.values()) {
            PostProcessorsType postprocessorsDef = def.getPostprocessors();
            List<PostProcessType> postprocessors = (postprocessorsDef == null) ? null
                    : postprocessorsDef.getPostprocessor();
            if (postprocessors == null) {
                continue;
            }
            for (PostProcessType postProcessType : postprocessors) {

                List<String> relatedFields = (postProcessType == null) ? null
                        : postProcessType.getFieldList();
                if (relatedFields == null) {
                    continue;
                }
                // Look up log elements that were renamed in the user configuration
                List<String> resolvedFields = new ArrayList<String>();
                for (String s : relatedFields) {
                    String name = placeholderToLogElementDefinitionMap.get(defaultNameToPlaceholderMap.get(s)).getName();
                    resolvedFields.add(name == null ? complexRegexType.getName() : name);
                }
                postProcessType.getFieldList().clear();
                postProcessType.getFieldList().addAll(resolvedFields);
            }
        }
    }

    /** Return a specified LogParser. */
    public LogParser getLogParser() {
        return new LogParser(logLineRegexp.toString(),
                Collections.unmodifiableList(new ArrayList<LogAttribute>(attributeOrder.values())),
                userdefined_charset, userdefined_charset2, creteUrlParameters);
    }

    /**
     * Processes complex log elements.
     *
     * This method replaces complex log element placeholders with their corresponding regular
     * expressions in the regular expression applied to log lines.
     *
     * @throws FileNotFoundException
     */
    private void processComplexConfigElements() {

        // handle quoted conf params as one unit
        Pattern p0 = Pattern.compile("\\\"([^\\\"]*)\\\\\"");
        Matcher m0 = p0.matcher(apacheLogFormat);
        while (m0.find()) {
            String configElem = m0.group(1);
            int startPos = logLineRegexp.indexOf(configElem);

            LogElementDefTypeArbitrary def = placeholderToLogElementDefinitionMap.get(configElem);

            String regexp;
            if (def == null || def.getRegex() == null) {
                // If the suitable don't exist than use the complexRegexType elements regexp (as like joker character),
                // so this way we can avoid the error.
                regexp = complexRegexType.getRegex();
            } else {
                regexp = def.getRegex();
            }

            logLineRegexp.replace(startPos, startPos + configElem.length(), regexp);

            // resolve attribute alias
            String attributeAlias = def == null ? complexRegexType.getName() : def.getName();
            attributeOrder.put(apacheLogFormat.indexOf(configElem),
                    new LogAttribute(attributeAlias, def));
        }
    }

    /**
     * Substitute regular expressions in individual log elements.
     *
     * Note: Log elements are defined in logdefinition.xml and user defined elements are in
     * config.xml.
     */
    private void processSimpleConfigElements() {

        for (Map.Entry<String, LogElementDefTypeArbitrary> entry : placeholderToLogElementDefinitionMap.entrySet()) {
            String configElem = entry.getKey();
            LogElementDefTypeArbitrary logElementDefType = entry.getValue();
            int i = logLineRegexp.indexOf(configElem);
            if (i < 0) {
                continue;
            }
            logLineRegexp.replace(i, i + configElem.length(), logElementDefType.getRegex());

            attributeOrder.put(apacheLogFormat.indexOf(configElem),
                    new LogAttribute(logElementDefType.getName(), logElementDefType));
        }
    }

    /**
     * Removes Apache LogFormat placeholder modifiers from the configuration line.
     *
     * Note: See Modifiers at http://httpd.apache.org/docs/X.X/mod/mod_log_config.html
     *
     * @param apacheLogFormat The LogFormat configuration line from Apache httpd configuration.
     * @return
     */
    public static String removePlaceholderModifiers(String apacheLogFormat) {
        Matcher m = modifierPattern.matcher(apacheLogFormat);
        StringBuilder sb = new StringBuilder(apacheLogFormat);
        while (m.find()) {

            String g1 = m.group(1);
            String g2 = m.group(2);
            if (g1 != null) {
                int idx = sb.indexOf(g1);
                sb.replace(idx, idx + g1.length(), "");
            }
            if (g2 != null) {
                int idx = sb.indexOf(g2);
                sb.replace(idx, idx + g2.length(), "");
            }
        }
        return sb.toString();
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    /**
     * Decode UrlDecodingCharsetEnumType what come from config.xml to String (it works with java.nio.charset.Charset too).
     *
     * Note: See valid Characterset: http://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html
     *
     * US-ASCII	Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
     * ISO-8859-1  	ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
     * UTF-8	Eight-bit UCS Transformation Format
     * UTF-16BE	Sixteen-bit UCS Transformation Format, big-endian byte order
     * UTF-16LE	Sixteen-bit UCS Transformation Format, little-endian byte order
     * UTF-16	Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
     *
     * @param UrlDecodingCharsetEnumType from user xml.
     * @return the String format of CharacterSet.
     */
    private String decode(UrlDecodingCharsetEnumType urlDecodingCharset) {
        if (urlDecodingCharset == null) return null;
        switch(urlDecodingCharset) {
            case ISO_8859_1: return "ISO-8859-1";
            case US_ASCII: return "US-ASCII";
            case UTF_16: return "UTF-16";
            case UTF_16_BE: return "UTF-16BE";
            case UTF_16_LE: return "UTF-16LE";
            case UTF_8:return "UTF-8";
            default: return null;
        }
    }
}
