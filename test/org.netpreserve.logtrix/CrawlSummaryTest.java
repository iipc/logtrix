package org.netpreserve.logtrix;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CrawlSummaryTest {

    @Test
    public void test() throws IOException {
        try (CrawlLogIterator log = new CrawlLogIterator(getClass().getResourceAsStream("crawl.log"))) {
            CrawlSummary summary = CrawlSummary.build(log);
            summary.print(System.out);
        }
    }

}