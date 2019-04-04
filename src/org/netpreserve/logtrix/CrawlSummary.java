package org.netpreserve.logtrix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.InternetDomainName;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static org.netpreserve.logtrix.CrawlLogUtils.canonicalizeMimeType;

public class CrawlSummary {

    private final Stats totals = new Stats();
    private final Map<Integer, Stats> statusCodes = new HashMap<>();
    private final Map<String, Stats> mimeTypes = new HashMap<>();

    /**
     * Builds a global crawl summary (not broken down).
     */
    public static CrawlSummary build(Iterable<CrawlDataItem> log) {
        CrawlSummary summary = new CrawlSummary();
        for (CrawlDataItem item : log) {
            summary.add(item);
        }
        return summary;
    }

    public static Map<String, CrawlSummary> byKey(Iterable<CrawlDataItem> log, Function<CrawlDataItem, String> keyFunction) {
        Map<String, CrawlSummary> map = new HashMap<>();
        for (CrawlDataItem item : log) {
            String key = keyFunction.apply(item);
            map.computeIfAbsent(key, k -> new CrawlSummary()).add(item);
        }
        return map;
    }

    public static Map<String, CrawlSummary> byHost(Iterable<CrawlDataItem> log) {
        return byKey(log, item -> {
            try {
                return URI.create(item.getURL()).getHost();
            } catch (Exception e) {
                return "";
            }
        });
    }

    public static Map<String, CrawlSummary> byRegisteredDomain(Iterable<CrawlDataItem> log) {
        return byKey(log, item -> {
            try {
                return InternetDomainName.from(URI.create(item.getURL()).getHost()).topPrivateDomain().toString();
            } catch (Exception e) {
                return "";
            }
        });
    }

    private void add(CrawlDataItem item) {
        String mimeType = canonicalizeMimeType(item.getMimeType());
        mimeTypes.computeIfAbsent(mimeType, m -> new Stats()).add(item);
        statusCodes.computeIfAbsent(item.getStatusCode(), s -> new Stats()).add(item);
        totals.add(item);
    }

    public Stats getTotals() {
        return totals;
    }

    public Map<Integer, Stats> getStatusCodes() {
        return statusCodes;
    }

    public Map<String, Stats> getMimeTypes() {
        return mimeTypes;
    }

    enum GroupBy {
        NONE, HOST, RDOMAIN
    }

    public static void main(String[] args) throws IOException {
        List<String> files = new ArrayList<>();
        Function<Iterable<CrawlDataItem>,Object> summarisier = CrawlSummary::build;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                switch (args[i]) {
                    case "-g":
                        switch (args[++i].toLowerCase(Locale.US)) {
                            case "host": summarisier = CrawlSummary::byHost; break;
                            case "registered-domain": summarisier = CrawlSummary::byRegisteredDomain; break;
                            default:
                                System.err.println("-g must be host or registered-domain");
                                System.exit(1);
                        }
                        break;
                    case "-h":
                    case "--help":
                        usage();
                        return;
                    default:
                        System.err.println("Unknown option: " + args[i]);
                        System.exit(1);
                        return;
                }
            } else {
                files.add(args[i]);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try (CrawlLogIterator log = new CrawlLogIterator(Paths.get(args[0]))) {
            objectMapper.writeValue(System.out, summarisier.apply(log));
        }
    }

    private static void usage() {
        System.out.println("Usage: CrawlSummary [options...] crawl.log\n" +
                "\n" +
                "Options:\n" +
                "  -g {host,registered-domain}   Group summary by host or registered domain");
    }
}
