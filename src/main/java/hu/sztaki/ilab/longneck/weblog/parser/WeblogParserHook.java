package hu.sztaki.ilab.longneck.weblog.parser;

import hu.sztaki.ilab.longneck.bootstrap.Hook;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;


/**
 * 
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
public class WeblogParserHook implements Hook, ResourceLoaderAware {
    
    private ResourceLoader resourceLoader;

    @Override
    public void init(Properties properties, ApplicationContext context) {
    	// Do nothing
    }

    @Override
    public List<URL> getSchemas() throws IOException {
        List<URL> schemas = new ArrayList<URL>(1);
        schemas.add(resourceLoader.getResource("classpath:META-INF/longneck/schema/longneck-weblog-parser.xsd").getURL());
         
        return schemas;
    }

    @Override
    public List<URL> getMappings() throws IOException {
        List<URL> mappings = new ArrayList<URL>(1);
        
        mappings.add(resourceLoader.getResource(
                "classpath:META-INF/longneck/castor/longneck-weblog-parser.mapping.xml").getURL());
        
        return mappings;
    }

    @Override
    public void setResourceLoader(ResourceLoader rl) {
        resourceLoader = rl;
    }
}
