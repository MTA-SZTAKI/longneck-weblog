
package hu.sztaki.ilab.longneck.weblog.parser.postprocessor;

import hu.sztaki.ilab.longneck.Field;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.LRUMap;

/**
 * Abstract super class to PostProcessor to implement the caching function.
 *
 * @author Molnár Péter <molnarp@ilab.sztaki.hu>
 */
abstract public class AbstractCachingPostProcessor {
    
    /** internal cache for request parameter splitting used by one thread */
    private ThreadLocal<Map<String, List<Field>>> threadLocalCache = null;

    protected List<Field> getCacheElement(String key) {
        return threadLocalCache.get().get(key);
    }

    public void putCacheElement(String key, List<Field> value) {
        threadLocalCache.get().put(key, value);
    }

    //@SuppressWarnings("unchecked")
    protected void initCache() {
        threadLocalCache = new ThreadLocal<Map<String, List<Field>>>() {
            @Override
            protected Map<String, List<Field>> initialValue() {
                return new LRUMap(100);
            }
        };
    }

    public boolean isCaching() {
        return (threadLocalCache != null);
    }
}
