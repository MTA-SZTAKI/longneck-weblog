package hu.sztaki.ilab.longneck.weblog.parser;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.access.Source;
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
@ContextConfiguration(locations = { "classpath:/parser-test.beans.xml", "classpath:/longneckWeblogParserContext.xml" })
public class WeblogFileTest extends AbstractJUnit4SpringContextTests {
    private ResourceLoader resourceLoader = new DefaultResourceLoader(this.getClass().getClassLoader());

    @Test
    public void simpleWeblogFileTest() throws SAXException, IOException, Exception {
        Source source = constructWeblogFileSourceSourceMock();
        source.init();

        Record record = source.getRecord();
        assertNotNull("No record has been created", record);

        checkFields(record, "logLine", "virtualhost 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\"");
    }


    public Source constructWeblogFileSourceSourceMock() throws IOException {
        WeblogFileSource source = (WeblogFileSource) applicationContext.getBean("weblog-file-source");
        assertNotNull("WeblogFileSource bean cannot be found!", source);

        source.setName("source1");
        Properties runtimeProperties = new Properties();
        runtimeProperties.load(resourceLoader.getResource("classpath:/weblog-test.properties").getInputStream());
        source.setRuntimeProperties(runtimeProperties);

        return source;
    }

    private void checkFields(Record r, String fieldname, String expectedValue) throws AssertionError {
        Field field = r.get(fieldname);
        assertNotNull("Field:" + fieldname + " cannot be found!", field);
        assertEquals("Extracted " + fieldname + " has invalid value!", field.getValue(), expectedValue);
    }
}
