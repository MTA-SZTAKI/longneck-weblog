package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.RecordImpl;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.weblog.parser.postprocessor.UrlParamsPostProcessor;
import hu.sztaki.ilab.longneck.weblog.parser.processor.LogParserFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gábor Lukács
 */
public class WeblogLineTest {

    static final String WEBLOGDEF =
            "virtualhost 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\"";

    static final String WEBLOGDEFOVER =
            "testregexp 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\"";

    static final String TSEARCHDEF =
            "virtualhost 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\" newfield1 newfield2";
    static final String UTF8Url =
            "virtualhost 86.101.8.36 - - [10/Mar/2013:09:00:26 +0100] "
            + "\"GET /biztosit/katker/kerekpar-biztositas.php HTTP/1.1\" 200 5852 "
            + "\"http://www.google.hu/url?sa=t&rct=j&q=ker%C3%A9kp%C3%A1r%20biztos%C3%ADt%C3%A1s&source=web"
            + "&cd=1&sqi=2&ved=0CEYQFjAA"
            + "&url=http%3A%2F%2Fvirtualhost%2Fbiztosit%2Fkatker%2Fkerekpar-biztositas.php"
            + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU\" "
            + "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17\" "
            + "\"Apache=180.99.50.118.1335426547348253; __utma=206900097.1859375732.1312987142.17468938690.1323470.3; "
            + "__utmz=123327697.134234270.3.5.utmgclid=CPqAm528urICFdd9hkAgQ|utmccn=(not%20set)|utmcmd=(not%20set)|"
            + "utmctr=kis%C3%A1llatbiztos%C3%ADt%C3%A1s\" "
            + "\"180.99.50.118.1335426547348253\"";
    static final String ISO88591Url =
            "virtualhost 86.101.8.36 - - [10/Mar/2013:09:00:26 +0100] "
            + "\"GET /biztosit/katker/kerekpar-biztositas.php HTTP/1.1\" 200 5852 "
            + "\"http://www.google.hu/url?sa=t&rct=j&q=ker%E9kp%E1r%20biztos%EDt%E1s\" "
            + "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17\" "
            + "\"Apache=180.99.50.118.1335426547348253; __utma=206900097.1859375732.1312987142.17468938690.1323470.3; "
            + "__utmz=123327697.134234270.3.5.utmgclid=CPqAm528urICFdd9hkAgQ|utmccn=(not%20set)|utmcmd=(not%20set)|"
            + "utmctr=kis%C3%A1llatbiztos%C3%ADt%C3%A1s\" "
            + "\"180.99.50.118.1335426547348253\"";
    Record record;

    public WeblogLineTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        record = new RecordImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of apply method, of class WeblogLine.
     */
    @Test
    public void testApply() throws Exception {
        record.add(new Field("LogLine", WEBLOGDEF));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat.test.xml");
        factory.afterPropertiesSet();

        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "virtualhost 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\"");

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

    /**
     * Test of apply method, of class WeblogLine.
     */
    @Test
    public void testApplyNaming() throws Exception {
        record.add(new Field("LogLine", WEBLOGDEF));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-naming.test.xml");
        factory.afterPropertiesSet();
        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "virtualhost 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\"");

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

        checkFields(record, "myrefererUrl", "http://virtualhost/");
        //postprocessor made field
        checkFields(record, "myrefererUrlFull", "http://virtualhost/");

        checkFields(record, "myRequest", "GET /index.php?lepes=2 HTTP/1.1");
        //postprocessor made field
        checkFields(record, "myRequestProtType", "HTTP/1.1");
        checkFields(record, "myRequestProtMethod", "GET");
        checkFields(record, "myRequestUrl", "http://virtualhost/index.php?lepes=2");
        //secound postprocessor made field
        checkFields(record, "myRequestUrlFull", "http://virtualhost/index.php?lepes=2");
        checkFields(record, "myRequestUrlExtension", "php");
        checkFields(record, "myRequestUrlParameter", "lepes=2");
        if (UrlParamsPostProcessor.DEFAULT_CREATE_PARAMETER_FIELDS) {
            checkFields(record, "myRequestUrlParameter-lepes", "2");
        }
    }

    /**
     * Test of apply method, of class WeblogLine.
     * Avoid override names bug.
     */
    @Test
    public void testApplyNaming2() throws Exception {
        record.add(new Field("LogLine", WEBLOGDEF));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-naming-2.test.xml");
        factory.afterPropertiesSet();
        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);

        checkFields(record, "LogLine", "virtualhost 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\"");

        checkFields(record, "bytesSent", "1358");
        checkFields(record, "clientip", "93.200.43.106");
        checkFields(record, "status", "200");
        checkFields(record, "userAgent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; "
                + ".NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)");
        checkFields(record, "responseCookie", "93.200.43.106.1328450248067000");
        checkFields(record, "identity", "-");
        checkFields(record, "time", "05/Feb/2012:14:58:38 +0100");
        checkFields(record, "myVirtualHost", "virtualhost");
        checkFields(record, "requestCookie", "PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000");
        checkFields(record, "user", "-");

        checkFields(record, "refererUrl", "http://virtualhost/");
        //postprocessor made field
        checkFields(record, "refererUrlFull", "http://virtualhost/");

        checkFields(record, "myRequest", "GET /index.php?lepes=2 HTTP/1.1");
        //postprocessor made field
        checkFields(record, "myRequestProtType", "HTTP/1.1");
        checkFields(record, "myRequestProtMethod", "GET");
        checkFields(record, "myRequestUrl", "http://virtualhost/index.php?lepes=2");
        //secound postprocessor made field
        checkFields(record, "myRequestUrlFull", "http://virtualhost/index.php?lepes=2");
        checkFields(record, "myRequestUrlExtension", "php");
        checkFields(record, "myRequestUrlParameter", "lepes=2");
        if (UrlParamsPostProcessor.DEFAULT_CREATE_PARAMETER_FIELDS) {
            checkFields(record, "myRequestUrlParameter-lepes", "2");
        }
    }

    /**
     * Test of apply method, of class WeblogLine.
     * Avoid override names with regexp.
     */
    @Test
    public void testApplyNaming3() throws Exception {
        record.add(new Field("LogLine", WEBLOGDEFOVER));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-naming-3.test.xml");
        factory.afterPropertiesSet();

        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "testregexp 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\"");

        checkFields(record, "bytesSent", "1358");
        checkFields(record, "clientip", "93.200.43.106");
        checkFields(record, "status", "200");
        checkFields(record, "userAgent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; "
                + ".NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)");
        checkFields(record, "responseCookie", "93.200.43.106.1328450248067000");
        checkFields(record, "identity", "-");
        checkFields(record, "time", "05/Feb/2012:14:58:38 +0100");
        checkFields(record, "myVirtualHost", "testregexp");
        checkFields(record, "requestCookie", "PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000");
        checkFields(record, "user", "-");

        checkFields(record, "refererUrl", "http://virtualhost/");
        //postprocessor made field
        checkFields(record, "refererUrlFull", "http://virtualhost/");

        checkFields(record, "myRequest", "GET /index.php?lepes=2 HTTP/1.1");
        //postprocessor made field
        checkFields(record, "myRequestProtType", "HTTP/1.1");
        checkFields(record, "myRequestProtMethod", "GET");
        checkFields(record, "myRequestUrl", "http://testregexp/index.php?lepes=2");
        //secound postprocessor made field
        checkFields(record, "myRequestUrlFull", "http://testregexp/index.php?lepes=2");
        checkFields(record, "myRequestUrlExtension", "php");
        checkFields(record, "myRequestUrlParameter", "lepes=2");
        if (UrlParamsPostProcessor.DEFAULT_CREATE_PARAMETER_FIELDS) {
            checkFields(record, "myRequestUrlParameter-lepes", "2");
        }
    }

    /**
     * Test of apply method, of class WeblogLine.
     * Test block in Tsearchlog, new field add to process.
     */
    @Test
    public void testApplyExtraFileds() throws Exception {
        record.add(new Field("LogLine", TSEARCHDEF));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-extra-fields.test.xml");
        factory.afterPropertiesSet();
        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "virtualhost 93.200.43.106 - - [05/Feb/2012:14:58:38 +0100] "
            + "\"GET /index.php?lepes=2 HTTP/1.1\" 200 1358 "
            + "\"http://virtualhost/\" "
            + "\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; .NET CLR 2.0.50727; "
            + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; AskTbASV5/5.11.3.15590; BRI/2)\" "
            + "\"PHPSESSID=j26oh4kq608nrpk62c1; Apache=93.200.43.106.1328450248067000\" "
            + "\"93.200.43.106.1328450248067000\" newfield1 newfield2");

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
        checkFields(record, "test1", "newfield1");
        checkFields(record, "test2", "newfield2");
    }

    /**
     * Test of apply method, of class WeblogLine.
     * Decoding UTF-8 parameter.
     */
    @Test
    public void testApplyUTF8URLs() throws Exception {
        record.add(new Field("LogLine", UTF8Url));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-url-param-utf8.test.xml");
        factory.afterPropertiesSet();

        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "virtualhost 86.101.8.36 - - [10/Mar/2013:09:00:26 +0100] "
            + "\"GET /biztosit/katker/kerekpar-biztositas.php HTTP/1.1\" 200 5852 "
            + "\"http://www.google.hu/url?sa=t&rct=j&q=ker%C3%A9kp%C3%A1r%20biztos%C3%ADt%C3%A1s&source=web&cd=1"
                + "&sqi=2&ved=0CEYQFjAA"
                + "&url=http%3A%2F%2Fvirtualhost%2Fbiztosit%2Fkatker%2Fkerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU\" "
            + "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17\" "
            + "\"Apache=180.99.50.118.1335426547348253; __utma=206900097.1859375732.1312987142.17468938690.1323470.3; "
                + "__utmz=123327697.134234270.3.5.utmgclid=CPqAm528urICFdd9hkAgQ|utmccn=(not%20set)|utmcmd=(not%20set)|"
                + "utmctr=kis%C3%A1llatbiztos%C3%ADt%C3%A1s\" "
            + "\"180.99.50.118.1335426547348253\"");


        checkFields(record, "refererUrl", "http://www.google.hu/url?sa=t&rct=j&q=ker%C3%A9kp%C3%A1r%20biztos%C3%ADt%C3%A1s&source=web"
                + "&cd=1&sqi=2&ved=0CEYQFjAA"
                + "&url=http%3A%2F%2Fvirtualhost%2Fbiztosit%2Fkatker%2Fkerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        //postprocessor made field
        checkFields(record, "refererUrlFull", "http://www.google.hu/url?sa=t&rct=j&q=kerékpár biztosítás&source=web&cd=1&sqi=2&ved=0CEYQFjAA"
                + "&url=http://virtualhost/biztosit/katker/kerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");

        checkFields(record, "refererUrlParameter", "sa=t&rct=j&q=kerékpár biztosítás&source=web&cd=1&sqi=2&ved=0CEYQFjAA"
                + "&url=http://virtualhost/biztosit/katker/kerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        // parameters:
        checkFields(record, "refererUrlParameter-sa", "t");
        checkFields(record, "refererUrlParameter-rct", "j");
        checkFields(record, "refererUrlParameter-q", "kerékpár biztosítás");
        checkFields(record, "refererUrlParameter-source", "web");
        checkFields(record, "refererUrlParameter-cd", "1");
        checkFields(record, "refererUrlParameter-sqi", "2");
        checkFields(record, "refererUrlParameter-ved", "0CEYQFjAA");
        checkFields(record, "refererUrlParameter-url", "http://virtualhost/biztosit/katker/kerekpar-biztositas.php");
        checkFields(record, "refererUrlParameter-usg", "AFQjCNGjcmn3sgK5TUYJ3vMyQ");
        checkFields(record, "refererUrlParameter-xax", "bv.43287494,d.ZWU");
    }

    /**
     * Test of apply method, of class WeblogLine.
     * Decoding ISO88591 parameter.
     */
    @Test
    public void testApplyIsoUrl() throws Exception {
        record.add(new Field("LogLine", ISO88591Url));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-url-param-iso.test.xml");
        factory.afterPropertiesSet();
        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "virtualhost 86.101.8.36 - - [10/Mar/2013:09:00:26 +0100] "
            + "\"GET /biztosit/katker/kerekpar-biztositas.php HTTP/1.1\" 200 5852 "
            + "\"http://www.google.hu/url?sa=t&rct=j&q=ker%E9kp%E1r%20biztos%EDt%E1s\" "
            + "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17\" "
            + "\"Apache=180.99.50.118.1335426547348253; __utma=206900097.1859375732.1312987142.17468938690.1323470.3; "
                + "__utmz=123327697.134234270.3.5.utmgclid=CPqAm528urICFdd9hkAgQ|utmccn=(not%20set)|"
                + "utmcmd=(not%20set)|utmctr=kis%C3%A1llatbiztos%C3%ADt%C3%A1s\" "
            + "\"180.99.50.118.1335426547348253\"");


        checkFields(record, "refererUrl", "http://www.google.hu/url?sa=t&rct=j&q=ker%E9kp%E1r%20biztos%EDt%E1s");
        //postprocessor made field
        checkFields(record, "refererUrlFull", "http://www.google.hu/url?sa=t&rct=j&q=kerékpár biztosítás");

        checkFields(record, "refererUrlParameter", "sa=t&rct=j&q=kerékpár biztosítás");
        // parameters:
        checkFields(record, "refererUrlParameter-sa", "t");
        checkFields(record, "refererUrlParameter-rct", "j");
        checkFields(record, "refererUrlParameter-q", "kerékpár biztosítás");
    }


    /**
     * Test of apply method, of class WeblogLine.
     * Decoding parameters both as UTF-8 and ISO88591 strings.
     */
    @Test
    public void testApplyDoubleEncoding() throws Exception {
        record.add(new Field("LogLine", UTF8Url));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-double-encoding.test.xml");
        factory.afterPropertiesSet();

        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "virtualhost 86.101.8.36 - - [10/Mar/2013:09:00:26 +0100] "
            + "\"GET /biztosit/katker/kerekpar-biztositas.php HTTP/1.1\" 200 5852 "
            + "\"http://www.google.hu/url?sa=t&rct=j&q=ker%C3%A9kp%C3%A1r%20biztos%C3%ADt%C3%A1s&source=web&cd=1"
                + "&sqi=2&ved=0CEYQFjAA"
                + "&url=http%3A%2F%2Fvirtualhost%2Fbiztosit%2Fkatker%2Fkerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU\" "
            + "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17\" "
            + "\"Apache=180.99.50.118.1335426547348253; __utma=206900097.1859375732.1312987142.17468938690.1323470.3; "
                + "__utmz=123327697.134234270.3.5.utmgclid=CPqAm528urICFdd9hkAgQ|utmccn=(not%20set)|utmcmd=(not%20set)|"
                + "utmctr=kis%C3%A1llatbiztos%C3%ADt%C3%A1s\" "
            + "\"180.99.50.118.1335426547348253\"");


        checkFields(record, "refererUrl", "http://www.google.hu/url?sa=t&rct=j"
                + "&q=ker%C3%A9kp%C3%A1r%20biztos%C3%ADt%C3%A1s&source=web&cd=1&sqi=2&ved=0CEYQFjAA"
                + "&url=http%3A%2F%2Fvirtualhost%2Fbiztosit%2Fkatker%2Fkerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        //postprocessor made field
        checkFields(record, "refererUrlFull", "http://www.google.hu/url?sa=t&rct=j&q=kerékpár biztosítás&source=web&cd=1"
                + "&sqi=2&ved=0CEYQFjAA"
                + "&url=http://virtualhost/biztosit/katker/kerekpar-biztositas.php&ei=kj08Ua22KhYHQBw"
                + "&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        checkFieldsexist(record, "refererUrlFull2");

        checkFields(record, "refererUrlParameter", "sa=t&rct=j&q=kerékpár biztosítás&source=web&cd=1&sqi=2&ved=0CEYQFjAA&url="
                + "http://virtualhost/biztosit/katker/kerekpar-biztositas.php&ei=kj08Ua22KhYHQBw"
                + "&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        checkFieldsexist(record, "refererUrlParameter2");
        // parameters:
        checkFields(record, "refererUrlParameter-sa", "t");
        checkFields(record, "refererUrlParameter-rct", "j");
        checkFields(record, "refererUrlParameter-q", "kerékpár biztosítás");
        checkFields(record, "refererUrlParameter-source", "web");
        checkFields(record, "refererUrlParameter-cd", "1");
        checkFields(record, "refererUrlParameter-sqi", "2");
        checkFields(record, "refererUrlParameter-ved", "0CEYQFjAA");
        checkFields(record, "refererUrlParameter-url", "http://virtualhost/biztosit/katker/kerekpar-biztositas.php");
        checkFields(record, "refererUrlParameter-usg", "AFQjCNGjcmn3sgK5TUYJ3vMyQ");
        checkFields(record, "refererUrlParameter-xax", "bv.43287494,d.ZWU");
        // parameters2:
        checkFields(record, "refererUrlParameter2-sa", "t");
        checkFields(record, "refererUrlParameter2-rct", "j");
        checkFieldsexist(record, "refererUrlParameter2-q");
        checkFields(record, "refererUrlParameter2-source", "web");
        checkFields(record, "refererUrlParameter2-cd", "1");
        checkFields(record, "refererUrlParameter2-sqi", "2");
        checkFields(record, "refererUrlParameter2-ved", "0CEYQFjAA");
        checkFieldsexist(record, "refererUrlParameter2-url");
        checkFields(record, "refererUrlParameter2-usg", "AFQjCNGjcmn3sgK5TUYJ3vMyQ");
        checkFields(record, "refererUrlParameter2-xax", "bv.43287494,d.ZWU");
    }

    /**
     * Test of apply method, of class WeblogLine.
     * Decoding UTF-8 and ISO88591 the parameter too.
     */
    @Test
    public void testApplyDoubleEncodingWithoutParameter() throws Exception {
        record.add(new Field("LogLine", UTF8Url));

        LogParserFactory factory = new LogParserFactory();
        factory.setConfigPath("src/test/resources/logformat-double-encoding-2.test.xml");
        factory.afterPropertiesSet();

        WeblogLine weblogblock = new WeblogLine();
        weblogblock.setLogParserFactory(factory);
        weblogblock.afterPropertiesSet();
        weblogblock.setApplyTo("LogLine");

        VariableSpace parentScope = null;
        weblogblock.apply(record, parentScope);

        assertNotNull("No record has been created", record);


        checkFields(record, "LogLine", "virtualhost 86.101.8.36 - - [10/Mar/2013:09:00:26 +0100] "
            + "\"GET /biztosit/katker/kerekpar-biztositas.php HTTP/1.1\" 200 5852 "
            + "\"http://www.google.hu/url?sa=t&rct=j&q=ker%C3%A9kp%C3%A1r%20biztos%C3%ADt%C3%A1s&source=web&cd=1&sqi=2"
                + "&ved=0CEYQFjAA"
                + "&url=http%3A%2F%2Fvirtualhost%2Fbiztosit%2Fkatker%2Fkerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU\" "
            + "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17\" "
            + "\"Apache=180.99.50.118.1335426547348253; __utma=206900097.1859375732.1312987142.17468938690.1323470.3; "
                + "__utmz=123327697.134234270.3.5.utmgclid=CPqAm528urICFdd9hkAgQ|utmccn=(not%20set)|"
                + "utmcmd=(not%20set)|utmctr=kis%C3%A1llatbiztos%C3%ADt%C3%A1s\" "
            + "\"180.99.50.118.1335426547348253\"");

        checkFields(record, "refererUrl", "http://www.google.hu/url?sa=t&rct=j&q=ker%C3%A9kp%C3%A1r%20biztos%C3%ADt%C3%A1s&"
                + "source=web&cd=1&sqi=2&ved=0CEYQFjAA&"
                + "url=http%3A%2F%2Fvirtualhost%2Fbiztosit%2Fkatker%2Fkerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        //postprocessor made field
        checkFields(record, "refererUrlFull", "http://www.google.hu/url?sa=t&rct=j&q=kerékpár biztosítás&source=web&cd=1"
                + "&sqi=2&ved=0CEYQFjAA"
                + "&url=http://virtualhost/biztosit/katker/kerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        checkFieldsexist(record, "refererUrlFull2");

        checkFields(record, "refererUrlParameter", "sa=t&rct=j&q=kerékpár biztosítás&source=web&cd=1&sqi=2&ved=0CEYQFjAA"
                + "&url=http://virtualhost/biztosit/katker/kerekpar-biztositas.php"
                + "&ei=kj08Ua22KhYHQBw&usg=AFQjCNGjcmn3sgK5TUYJ3vMyQ&xax=bv.43287494,d.ZWU");
        // parameters:
        checkFieldsnotexist(record, "refererUrlParameter-q");
        // parameters2:
        checkFieldsnotexist(record, "refererUrlParameter2-q");
    }

    void checkFields(Record r, String fieldname, String expectedValue) throws AssertionError {
        Field field = r.get(fieldname);
        assertNotNull("Field:" + fieldname + " cannot be found!", field);
        assertEquals("Extracted " + fieldname + " has invalid value!", field.getValue(), expectedValue);
    }

    void checkFieldsexist(Record r, String fieldname) throws AssertionError {
        Field field = r.get(fieldname);
        assertNotNull("Field:" + fieldname + " cannot be found!", field);
    }

    void checkFieldsnotexist(Record r, String fieldname) throws AssertionError {
        Field field = r.get(fieldname);
        assertNull("Field:" + fieldname + " is exit but it shoudn't!", field);
    }
}
