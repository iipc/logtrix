package org.netpreserve.logtrix;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CrawlLogIteratorTest {

    @Test
    public void test() throws IOException {
        try (CrawlLogIterator log = new CrawlLogIterator(getClass().getResourceAsStream("crawl.log"))) {
            for (CrawlDataItem item: log) {
                System.out.println(item.getURL());
            }
        }
    }

}