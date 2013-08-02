package hu.sztaki.ilab.longneck.weblog.parser;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.RecordImpl;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.access.NoMoreRecordsException;
import hu.sztaki.ilab.longneck.weblog.parser.processor.LogParser;
import hu.sztaki.ilab.longneck.weblog.parser.processor.LogParserFactory;
import java.util.InputMismatchException;
import org.apache.log4j.Logger;

/**
 * Main class that defines a weblog parser as a data source.
 *
 * This parser provides 1) a data source: WeblogFileSource gives a field with a
 * name logLine as one line of the file. 2) a block: WeblogLine processes
 * logLin, and produces derived fields.
 *
 * Note: If the parser fails, no error messages appear in the error output. If
 * it is expected use the 2 steps process.
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 *
 */
public class WeblogParserSource extends AbstractLogFileSource {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(WeblogParserSource.class);
    /**
     * The log parser factory.
     */
    private LogParserFactory logParserFactory;
    /**
     * The log parser object.
     */
    private LogParser logParser;

    @Override
    public void init() {
        // Call parent initializer
        super.init();

        // Create log parser
        logParser = logParserFactory.getLogParser();
    }

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
        try {
            logParser.doProcess(result, oneData);
        } catch (InputMismatchException e) {
            logParser.LOG.error(e);
            result = null;
        } catch (CheckError ex) {
            LOG.error(ex);
            result = null;
        }

        return result;
    }

    @Override
    public void close() {
    }

    public LogParserFactory getLogParserFactory() {
        return logParserFactory;
    }

    public void setLogParserFactory(LogParserFactory logParserFactory) {
        this.logParserFactory = logParserFactory;
    }
}
