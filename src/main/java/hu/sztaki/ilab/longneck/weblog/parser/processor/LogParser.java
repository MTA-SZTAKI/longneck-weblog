package hu.sztaki.ilab.longneck.weblog.parser.processor;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import hu.sztaki.ilab.longneck.weblog.parser.postprocessor.CharcodingInterface;
import hu.sztaki.ilab.longneck.weblog.parser.postprocessor.PostProcessor;
import hu.sztaki.ilab.longneck.weblog.parser.postprocessor.PostProcessorFactory;
import hu.sztaki.ilab.longneck.weblog.parser.postprocessor.UrlParamsPostProcessor;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import parser.PostProcessEnumType;

/**
 * Parses and processes one log line based on the the configuration parameters.
 *
 * This implementation is thread-safe.
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
public class LogParser extends AbstractSourceInfoContainer {

    public static final Logger LOG = Logger.getLogger(LogParser.class);

	/** Compiled pattern to be applied on the log files. */
	public final Pattern appliedPattern;
	/** Unmodifiable list of log attributes. */
	private final List<LogAttribute> attributesList;
    /** The supposed character encoding of the logline. User can set this value in the config.xml.*/
    String userdefined_charset = null;
    /** The 2. supposed character encoding of the logline in the case when mixing encoding occur.
     * User can set this value in the config.xml.*/
    String userdefined_charset2 = null;
    /** In case the Url parameter fields will add to the fields. */
    Boolean creteUrlParameters = null;
    /** Default value of caching when hadn't been defined by user is a config.xml. */
    private static final boolean DEFAULT_CACHING = false;

	public LogParser(String logLineRegexp, List<LogAttribute> attributesList) {
        appliedPattern = Pattern.compile(logLineRegexp);
        LOG.debug("LogParser logline pattern: " + appliedPattern.toString()) ;
        this.attributesList = attributesList;
	}

    /** Constructor to be able to give user defined characterset(s).*/
	public LogParser(String logLineRegexp, List<LogAttribute> attributesList, String userdefined_charset,
            String userdefined_charset2, Boolean creteUrlParameters) {
        appliedPattern = Pattern.compile(logLineRegexp);
        LOG.debug("LogParser logline pattern: " + appliedPattern.toString()) ;
        this.attributesList = attributesList;
        this.userdefined_charset = userdefined_charset;
        this.userdefined_charset2 = userdefined_charset2;
        this.creteUrlParameters = creteUrlParameters;
	}

	/**
	 * Parses on log line based on the configuration parameters
	 *
	 * @param input - one log line
	 * @return Processed Record
	 * @throws CheckFailureException
	 */
	public void doProcess(Record result, String input) throws CheckError {
        if (input == null) {
            return;
        }

        Matcher m1 = appliedPattern.matcher(input);
        /** Replace the the null character (%00) to space character (%20), because this character make error during progress.
         *  A null character can be placed in a URL with %00,
         *  which (in case of unchecked user input) creates a vulnerability known
         *  as null byte injection and can lead to security exploits.
         *  See this to more knowledge:
         *  http://projects.webappsec.org/w/page/13246949/Null%20Byte%20Injection
         */
        input = input.replaceAll("%00", "%20");

            if (!m1.matches() || attributesList.size() != m1.groupCount()) {
                // Not match the line to expected format.
                throw new InputMismatchException(String.format(
                        "Log format mismatch, weblog parser is unable to parse line:\n '%1$s'\n", input));
            }

            for (int i = 1; i != m1.groupCount() + 1; i++) {

                String processedItem = m1.group(i);
                LogAttribute logAttribute = attributesList.get(i - 1);

                Field f = new Field(logAttribute.getAttributeAlias(), processedItem);
                result.add(f);

                List<ImmutablePostProcessType> postprocessors = logAttribute.getPostprocessors();

                // Apply each log attribute postprocessor
                for (ImmutablePostProcessType p : postprocessors) {
                    PostProcessEnumType pType = p.getType();
                    if (PostProcessEnumType.NONE != pType) {
                        PostProcessor postprocessor = PostProcessorFactory.get(pType);
                        String elementName = getElementName(logAttribute, p);

                        // Check if a given postprocessor implement a CharcodingInterface or not,
                        // than - if less one of the charactersets is setted - it will set the charactersets
                        // trought the CharcodingInterface.
                        if (postprocessor instanceof CharcodingInterface) {
                            ((CharcodingInterface) postprocessor).setCharset(userdefined_charset);
                            ((CharcodingInterface) postprocessor).setCharset2(userdefined_charset2);
                        }

                        // Set the createUrlParameter to the UrlParamsPostProcessor if it is given by user.
                        // The default is true.
                        if (postprocessor instanceof UrlParamsPostProcessor) {
                            ((UrlParamsPostProcessor) postprocessor).setUserCreateParameterFiled(creteUrlParameters);
                        }

                        postprocessor.doPostProcess(elementName,
                                BlockUtils.getValue(elementName, result, null), p.getFieldList(),
                                result, p.isCaching() != null?p.isCaching():DEFAULT_CACHING);
                    }
                }
            }

    }

	/**
	 * Returns the name of the Field on which the postprocessor operates.
	 * If <apply-to-postfixed> is set on the given postprocessor (see logdefinition.xml)
	 * then its name is added as a postfix to the name of the log attribute. Otherwise the
	 * name log attribute is applied
	 *
	 * @param logAttribute Log attribute being processed
	 * @param p Postprocessor type
	 * @return The concatenated name.
	 */
    public static String getElementName(LogAttribute logAttribute,
            ImmutablePostProcessType postProcessType) {

        String postfixed = postProcessType.getApplyToPostfixed();
        String attrName = logAttribute.getAttributeAlias();

        return (postfixed == null) ? attrName : (attrName + postfixed);
    }
}
