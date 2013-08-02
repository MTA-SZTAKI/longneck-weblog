package hu.sztaki.ilab.longneck.weblog.parser.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import parser.LogElementDefTypeArbitrary;
import parser.PostProcessType;

/**
 * Log attribute descriptor class.
 *
 * This class is immutable.
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
public class LogAttribute {

	private final String attributeAlias;

	/** Postprocessor types are defined in parser.xsd */
	private final List<ImmutablePostProcessType> postprocessors;

    public LogAttribute(String attributeAlias, LogElementDefTypeArbitrary logElementDefType) {
        this.attributeAlias = attributeAlias;

        ArrayList<ImmutablePostProcessType> tmpPostProcessors = new ArrayList<ImmutablePostProcessType>();

        if (logElementDefType != null &&
                logElementDefType.getPostprocessors() != null &&
                logElementDefType.getPostprocessors().getPostprocessor() != null) {
            for (PostProcessType p : logElementDefType.getPostprocessors().getPostprocessor()) {
                tmpPostProcessors.add(new ImmutablePostProcessType(p));
            }
        }

        postprocessors = Collections.unmodifiableList(tmpPostProcessors);
    }

	public String getAttributeAlias() {
		return attributeAlias;
	}

	public List<ImmutablePostProcessType> getPostprocessors() {
        return postprocessors;
    }

}
