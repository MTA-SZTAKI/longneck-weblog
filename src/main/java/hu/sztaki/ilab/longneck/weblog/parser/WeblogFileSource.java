package hu.sztaki.ilab.longneck.weblog.parser;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.RecordImpl;
import hu.sztaki.ilab.longneck.process.access.NoMoreRecordsException;
import org.apache.log4j.Logger;

/**
 * Reads a log file line by line.
 *
 * This class reads a log file one line at a time, and creates a record with a single field
 * "logLine", that contains the log line.
 *
 * @author Molnár Péter <molnarp@ilab.sztaki.hu>
 */
public class WeblogFileSource extends AbstractLogFileSource {

    /** Logger. */
	private static final Logger LOG = Logger.getLogger(WeblogParserSource.class);

    @Override
    public Record getRecord() throws NoMoreRecordsException {
		Record result = new RecordImpl();
        //source is prefetched and queried one-by-one
        String oneData;
        if (dataSegment == null || dataSegment.isEmpty()) {
            dataSegment = bf.getDataSegment();
        }

        if (dataSegment.isEmpty()) {
            throw new NoMoreRecordsException("No more records to read");
        }

        oneData = dataSegment.removeFirst();

        if (oneData == null) {
            throw new NoMoreRecordsException("No more records to read");
        }

        result.add(new Field("logLine", oneData));
		return result;
    }

    @Override
    public void close() {
    }

}
