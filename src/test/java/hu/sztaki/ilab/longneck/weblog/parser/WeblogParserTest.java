package hu.sztaki.ilab.longneck.weblog.parser;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.access.Source;
import hu.sztaki.ilab.longneck.weblog.parser.postprocessor.UrlParamsPostProcessor;
import hu.sztaki.ilab.longneck.weblog.parser.processor.LogParserFactory;
import java.io.IOException;
import java.util.Properties;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 *
 * @author Gábor Lukács
 */
@ContextConfiguration(locations = {"classpath:/parser-test.beans.xml", "classpath:/longneckWeblogParserContext.xml"})
public class WeblogParserTest extends AbstractJUnit4SpringContextTests
{

    private ResourceLoader resourceLoader = new DefaultResourceLoader(this.getClass().getClassLoader());

    @Test
    public void simpleWeblogParserTest() throws SAXException, IOException, Exception {
        Source source = constructWeblogParserSourceMock();
        source.init();

        Record record = source.getRecord();
        assertNotNull("No record has been created", record);

        checkFields(record, "bytesSent", "1358");
        checkFields(record, "clientip", "93.200.43.106");
        checkFields(record, "status", "200");
        checkFields(record, "userAgent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; "
                + ".NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)");
        checkFields(record, "responseCookie", "93.200.43.106.1328450248067000");
        checkFields(record, "identity", "-");
        checkFields(record, "time", "05/Feb/2012:14:58:38 +0100");
        checkFields(record, "virtualhost", "virtualhost");
        checkFields(record, "requestCookie", "PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000");
        checkFields(record, "user", "-");

        checkFields(record, "refererUrl", "http://virtualhost/");
        //postprocessor made field
        checkFields(record, "refererUrlFull", "http://virtualhost/");

        checkFields(record, "request", "GET /index.php?lepes=2 HTTP/1.1");
        //postprocessor made field
        checkFields(record, "requestProtType", "HTTP/1.1");
        checkFields(record, "requestProtMethod", "GET");
        checkFields(record, "requestUrl", "http://virtualhost/index.php?lepes=2");
        //secound postprocessor made field
        checkFields(record, "requestUrlFull", "http://virtualhost/index.php?lepes=2");
        checkFields(record, "requestUrlExtension", "php");
        checkFields(record, "requestUrlParameter", "lepes=2");
        if (UrlParamsPostProcessor.DEFAULT_CREATE_PARAMETER_FIELDS) {
            checkFields(record, "requestUrlParameter-lepes", "2");
        }
    }

    public Source constructWeblogParserSourceMock() throws IOException {
        WeblogParserSource source = (WeblogParserSource) applicationContext.getBean("weblog-parser-source");
        assertNotNull("WeblogParserSource bean cannot be found!", source);

        source.setName("source1");
        Properties runtimeProperties = new Properties();
        runtimeProperties.load(resourceLoader.getResource("classpath:/weblog-test.properties").getInputStream());

        source.setRuntimeProperties(runtimeProperties);

        LogParserFactory logParserFactory = (LogParserFactory) applicationContext.getBean("log-parser-factory");

        source.setLogParserFactory(logParserFactory);

        return source;
    }

    private void checkFields(Record r, String fieldname, String expectedValue) throws AssertionError {
        Field field = r.get(fieldname);
        assertNotNull("Field:" + fieldname + " cannot be found!", field);
        assertEquals("Extracted " + fieldname + " has invalid value!", field.getValue(), expectedValue);
    }
}
