package hu.sztaki.ilab.longneck.weblog.parser.postprocessor;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Postprocessor that extracts protocol method and protocol type from the
 * request parameter and builds a proper request url.
 *
 * Note: No info on http / https protocols! All url-s will begin wiht http:// now.
 *
 * This class is thread-safe.
 *
 * E.g:
 *
 * <pre>
 * Input:
 * virtualhost:         www.example.com
 * request:             POST /mysite/index.php?id=123&value=abc HTTP/1.1
 *
 * Output:
 * virtualhost:         www.example.com
 * request:             POST /mysite/index.php?id=123&value=abc HTTP/1.1
 * requestUrl:          http://www.example.com/mysite/index.php?id=123&value=abc
 * requestProtMethod:   POST
 * requestProtType:     HTTP/1.1
 *
 * <pre>
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
public class SplitRequestPostProcessor extends AbstractCachingPostProcessor implements PostProcessor {

    private static final Logger LOG = Logger.getLogger(SplitRequestPostProcessor.class);

    private final Pattern reqSplitPattern = Pattern.compile("^(\\S+)\\s*(\\/*\\S+)\\s*(\\S*)$");

    private final String ATTR_REQ_PROT_METHOD_POSTFIX = "ProtMethod";

    private final String ATTR_REQ_PROT_TYPE_POSTFIX = "ProtType";

    private final String ATTR_REQ_URL = "Url";

    /**
     * Extracts protocol method and protocol type from the
     * request parameter as well as builds request url.
     *
     * @see hu.sztaki.ilab.longneck.weblog.parser.postprocessor.PostProcessor#doPostProcess(
     * java.lang.String, hu.sztaki.ilab.longneck.weblog.parser.processor.LogAttribute, hu.sztaki.ilab.longneck.Record)
     */
    @Override
    public void doPostProcess(String elementName, String elementValue, List<String> relatedFields,
            Record record, boolean caching) {

        try {

            if (elementValue == null) {
                LOG.warn(String.format("Couldn't postprocess field %1$s . No value present", elementName));
                return;
            }

            // fetch virtualhost field using the postprocessor's field definition
            String virtualhostFieldName = relatedFields.get(0); // only one field should be defined
            Field virtualhostField = record.get(virtualhostFieldName);

            if (virtualhostField == null) {
                throw new Exception("Can't find virtualhost field named \"" + virtualhostFieldName + "\"");
            }

            if (caching) {
                // false if the cash is not initialized yet.
                if (!this.isCaching()) {
                    initCache();
                } else {
                    List<Field> splitFields;
                    splitFields = getCacheElement(elementValue);
                    // cache hit
                    if (splitFields != null) {
                        for (Field f : splitFields) {
                            record.add(f);
                        }
                        return;
                    }
                }
            }


            Field reqMethodField = new Field(elementName + ATTR_REQ_PROT_METHOD_POSTFIX);
            Field reqUrl = new Field(elementName + ATTR_REQ_URL);
            Field reqTypeField = new Field(elementName + ATTR_REQ_PROT_TYPE_POSTFIX);

            Matcher m = reqSplitPattern.matcher(elementValue);
            if (m.matches()) {

                reqMethodField.setValue(m.group(1));
                // Note: No info on http / https protocols! All url-s will begin wiht http:// now.
                reqUrl.setValue("http://" + virtualhostField.getValue() + m.group(2));
                reqTypeField.setValue(m.group(3));

                if (caching && this.isCaching()) {
                    List<Field> splitFields;
                    splitFields = new ArrayList<Field>(3);
                    splitFields.add(reqMethodField);
                    splitFields.add(reqUrl);
                    splitFields.add(reqTypeField);

                    putCacheElement(elementValue, splitFields);
                }
                record.add(reqMethodField);
                record.add(reqUrl);
                record.add(reqTypeField);

            }
            else {
                LOG.debug("Request is invalid or non-HTTP compliant! " + elementValue);
            }

        } catch (Exception e) {
            LOG.error("Couldn't postprocess request parameter: " + elementValue);
        }

    }
}
