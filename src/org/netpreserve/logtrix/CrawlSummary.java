package org.netpreserve.logtrix;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.InternetDomainName;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;
import static org.netpreserve.logtrix.CrawlLogUtils.canonicalizeMimeType;

public class CrawlSummary {

    private final Stats totals = new Stats();
    private Map<Integer, Stats> statusCodes = new HashMap<>();
    private Map<String, Stats> mimeTypes = new HashMap<>();

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

    /**
     * Builds a crawl summary grouped by the given key function.
     */
    public static Map<String, CrawlSummary> groupedBy(Iterable<CrawlDataItem> log, Function<CrawlDataItem, String> keyFunction) {
        Map<String, CrawlSummary> map = new HashMap<>();
        for (CrawlDataItem item : log) {
            String key = keyFunction.apply(item);
            map.computeIfAbsent(key, k -> new CrawlSummary()).add(item);
        }
        return map;
    }

    public static Map<String, CrawlSummary> byHost(Iterable<CrawlDataItem> log) {
        return groupedBy(log, item -> {
            try {
                return URI.create(item.getURL()).getHost();
            } catch (Exception e) {
                return "";
            }
        });
    }

    public static Map<String, CrawlSummary> byRegisteredDomain(Iterable<CrawlDataItem> log) {
        return groupedBy(log, item -> {
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
        statusCodes.computeIfAbsent(item.getStatusCode(), code -> new Stats(StatusCodes.describe(code))).add(item);
        totals.add(item);
    }

    /**
     * Returns a new CrawlSummary with the mime-type and status-code lists limited to the top-N results.
     */
    public CrawlSummary topN(long n) {
        CrawlSummary summary = new CrawlSummary();
        summary.mimeTypes = mimeTypes.entrySet().stream().parallel()
                .sorted(comparing(e -> -e.getValue().getCount()))
                .limit(n)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> b,
                        LinkedHashMap::new));
        summary.statusCodes = statusCodes.entrySet().stream().parallel()
                .sorted(comparing(e -> -e.getValue().getCount()))
                .limit(n)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> b,
                        LinkedHashMap::new));
        return summary;
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

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        List<String> files = new ArrayList<>();
        Function<Iterable<CrawlDataItem>, Object> summarisier = CrawlSummary::build;
        long topN = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                switch (args[i]) {
                    case "-g":
                        switch (args[++i].toLowerCase(Locale.US)) {
                            case "host":
                                summarisier = CrawlSummary::byHost;
                                break;
                            case "registered-domain":
                                summarisier = CrawlSummary::byRegisteredDomain;
                                break;
                            default:
                                System.err.println("-g must be host or registered-domain");
                                System.exit(1);
                        }
                        break;
                    case "-n":
                        topN = Long.parseLong(args[++i]);
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
        objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(NON_NULL);

        try (CrawlLogIterator log = new CrawlLogIterator(Paths.get(files.get(0)))) {
            Object summary = summarisier.apply(log);

            // limit to top N results
            if (topN > 0) {
                if (summary instanceof Map) {
                    Map<Object, CrawlSummary> map = new HashMap<>();
                    for (Map.Entry<Object, CrawlSummary> entry : ((Map<Object, CrawlSummary>) summary).entrySet()) {
                        map.put(entry.getKey(), entry.getValue().topN(topN));
                    }
                    summary = map;
                } else if (summary instanceof CrawlSummary) {
                    summary = ((CrawlSummary) summary).topN(topN);
                } else {
                    throw new AssertionError("unexpected");
                }
            }

            objectMapper.writeValue(System.out, summary);
        }
    }

    private static void usage() {
        System.out.println("Usage: CrawlSummary [options...] crawl.log\n" +
                "\n" +
                "Options:\n" +
                "  -g {host,registered-domain}   Group summary by host or registered domain\n" +
                "  -n N                          Limit to top N results");
    }
}
