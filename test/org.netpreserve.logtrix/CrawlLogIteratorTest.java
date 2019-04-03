package org.netpreserve.logtrix;

import java.io.IOException;

import static org.junit.Assert.*;

public class CrawlLogIteratorTest {

    public void test() throws IOException {
        try (CrawlLogIterator log = new CrawlLogIterator("crawl.log")) {
            for (CrawlDataItem item: log) {
                System.out.println(item.getURL());
            }
        }
    }

}