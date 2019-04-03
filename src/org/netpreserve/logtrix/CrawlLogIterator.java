package org.netpreserve.logtrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CrawlLogIterator implements AutoCloseable, Iterable<CrawlDataItem>, Iterator<CrawlDataItem> {
    private static final DateTimeFormatter OLD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").withZone(ZoneOffset.UTC);
    private static final Logger log = LoggerFactory.getLogger(CrawlLogIterator.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 
     * A reader for the crawl.log file being processed
     */
    BufferedReader in;
    
    /**
     * The next item to be issued (if ready) or null if the next item
     * has not been prepared or there are no more elements 
     */
    CrawlDataItem next;

    /**
     * Create a new CrawlLogIterator that reads items from a Heritrix crawl.log
     *
     * @param path The path of a Heritrix crawl.log file.
     * @throws IOException If errors were found reading the log.
     */
    public CrawlLogIterator(Path path) throws IOException {
        this(Files.newBufferedReader(path));
    }

    public CrawlLogIterator(Reader reader) {
        if (reader instanceof BufferedReader) {
            in = (BufferedReader) reader;
        } else {
            in = new BufferedReader(reader);
        }
    }

    public CrawlLogIterator(InputStream stream) {
        this(new InputStreamReader(stream));
    }

    /** 
     * Returns true if there are more items available.
     *
     * @return True if at least one more item can be fetched with next().
     */
    public boolean hasNext() {
        if(next == null){
            try {
                prepareNext();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return next!=null;
    }

    /** 
     * Returns the next valid item from the crawl log.
     *
     * @return An item from the crawl log.  Note that unlike the Iterator
     *         interface, this method returns null if there are no more items 
     *         to fetch.
     * @throws IOException If there is an error reading the item *after* the
     *         item to be returned from the crawl.log.
     * @throws NoSuchElementException If there are no more items 
     */
    public CrawlDataItem next() {
        if(hasNext()){
            CrawlDataItem tmp = next;
            this.next = null;
            return tmp;
        }
        throw new NoSuchElementException("No more items");
    }

    /**
     * Ready the next item.  This method will skip over items that
     * getNextItem() rejects.  When the method returns, either next is non-null
     * or there are no more items in the crawl log.
     * <p>
     * Note: This method should only be called when <code>next==null<code>
     */
    protected void prepareNext() throws IOException{
        String line;
        while ((line = in.readLine()) != null) {
            next = parseLine(line);
            if (next != null) {
                return;
            }
        }
     }

    /** 
     * Parse the a line in the crawl log.
     * <p>
     * Override this method to change how individual crawl log
     * items are processed and accepted/rejected.  This method is called from
     * within the loop in prepareNext().
     *
     * @param line A line from the crawl log.  Must not be null.
     * @return A {@link CrawlDataItem} if the next line in the crawl log yielded 
     *         a usable item, null otherwise.
     */
    protected CrawlDataItem parseLine(String line) throws IOException {
        CrawlDataItem cdi = new CrawlDataItem();

        if (line != null && line.length() > 42) {
            // Split the line up by white spaces.
            // Limit to 12 parts (annotations may contain spaces, but will
            // always be at the end of each line.
        	String[] lineParts = line.split("\\s+",13);
            
            if(lineParts.length<10){
                // If the lineParts are fewer then 10 then the line is 
                // malformed.
                return null;
            }
            
            // Index 0: Timestamp 
            try {
                // Convert from crawl.log format to Instant
            	if (lineParts[0].contains("T")){
            	    cdi.setTimestamp(Instant.parse(lineParts[0]));
            	} else {
            	    cdi.setTimestamp(Instant.from(OLD_DATE_FORMAT.parse(lineParts[0])));
            	}
            } catch (DateTimeParseException e) {
                log.error("Error parsing date for: {}", line, e);
                return null;
            }

            // Index 1: status return code 
            cdi.setStatusCode(lineParts[1]);
            
            // Index 2: File size 
            long size = -1;
            if (lineParts[2].equals("-")) {
            	size = 0;
            } else {
	            try {
	            	size = Long.parseLong(lineParts[2]);
	            } catch (NumberFormatException e) {
	            	log.error("Error parsing size for: {}. Item: {}", line, lineParts[2], e); 
	            	return null;
	            }
            }
            cdi.setSize(size);

            // Index 3: URL
            cdi.setURL(lineParts[3]);
            
            // Index 4: Hop path
            cdi.setHoppath(lineParts[4]);
            
            // Index 5: Parent URL
            cdi.setParentURL(lineParts[5]);
            
            // Index 6: Mime type
            cdi.setMimeType(lineParts[6]);

            // Index 7: ToeThread number (ignore)

            // Index 8: ArcTimeAndDuration
            {
                String timeAndDuration = lineParts[8];
                int i = timeAndDuration.indexOf('+');
                String time = timeAndDuration.substring(0, i);
                int millis = Integer.parseInt(timeAndDuration.substring(i + 1));
                cdi.setCaptureBegan(Instant.from(OLD_DATE_FORMAT.parse(time)));
                cdi.setDuration(Duration.of(millis, ChronoUnit.MILLIS));
            }

            // Index 9: Digest
            String digest = lineParts[9];
            // The digest may contain a prefix. 
            // The prefix will be terminated by a : which is immediately 
            // followed by the actual digest
			if (digest.lastIndexOf(":") >= 0) {
            	digest = digest.substring(digest.lastIndexOf(":")+1);
            }
            cdi.setContentDigest(digest);
            
            // Index 10: Source tag (ignore)
            if (!lineParts[10].equals("-")) {
                cdi.setSourceSeedUrl(lineParts[10]);
            } // else sourceTagSeeds was disabled
            
            // Index 11: Annotations (may be missing)
            boolean revisit = false;
        	if (lineParts[11].contains("Revisit")) {
        		revisit=true;
        	}
            cdi.setDuplicate(revisit);
            
        	if(revisit && lineParts.length==13){
        	    cdi.setExtraInfo(objectMapper.readTree(lineParts[12]));
        	}

            // Got a valid item.
            cdi.setOriginalCrawlLogLine(line);
            return cdi;
        } 
        return null;
    }
    
    /**
     * Closes the crawl.log file.
     */
    public void close() throws IOException{
        in.close();
    }

    @Override
    public Iterator<CrawlDataItem> iterator() {
        return this;
    }
}
