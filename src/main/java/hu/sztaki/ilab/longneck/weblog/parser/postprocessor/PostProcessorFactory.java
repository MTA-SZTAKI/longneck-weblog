package hu.sztaki.ilab.longneck.weblog.parser.postprocessor;

import java.util.EnumMap;
import java.util.Map;
import parser.PostProcessEnumType;

/**
 * Postprocessor enum factory class.
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
public enum PostProcessorFactory {

	INSTANCE;

	private static final Map<PostProcessEnumType, PostProcessor> lookup =
		new EnumMap<PostProcessEnumType, PostProcessor>(PostProcessEnumType.class);

	static {
		lookup.put(PostProcessEnumType.URL_PARAMS, new UrlParamsPostProcessor());
		lookup.put(PostProcessEnumType.SPLIT_REQUEST, new SplitRequestPostProcessor());
	}

	public static PostProcessor get(PostProcessEnumType postProcessEnumType) {
		return lookup.get(postProcessEnumType);

	}

}
