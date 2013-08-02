package hu.sztaki.ilab.longneck.weblog.parser.postprocessor;

import hu.sztaki.ilab.longneck.Record;

import java.util.List;

/**
 * Postprocessor definition.
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
public interface PostProcessor {

	/**
	 * @param elementName - Name of the Field that is being processed
	 * @param elementValue - Field value
	 * @param relatedFields - Name of other Fields needed for the processing
	 * @param record - Record
	 */
	public void doPostProcess(String elementName, String elementValue, List<String> relatedFields, Record record, boolean caching);

}
