package hu.sztaki.ilab.longneck.weblog.parser.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import parser.PostProcessEnumType;
import parser.PostProcessType;

/**
 *
 * @author Molnár Péter <molnarp@ilab.sztaki.hu>
 */
public class ImmutablePostProcessType {
    private final String applyToPostfixed;
    private final PostProcessEnumType type;
    private final List<String> fieldList;
    private final Boolean caching;
    
    public ImmutablePostProcessType(PostProcessType postProcessType) {
        applyToPostfixed = postProcessType.getApplyToPostfixed();
        type = postProcessType.getType();
        fieldList = Collections.unmodifiableList(postProcessType.getFieldList() == null ? 
                new ArrayList<String>() : postProcessType.getFieldList());
        caching = postProcessType.isCaching();
    }

    public String getApplyToPostfixed() {
        return applyToPostfixed;
    }

    public PostProcessEnumType getType() {
        return type;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public Boolean isCaching() {
        return caching;
    }
}
