logtrix
=======

Examples
--------

### Parsing a log file

```java
try (CrawlLogIterator log = new CrawlLogIterator(Paths.get("crawl.log"))) {
    for (CrawlDataItem line : log) {
        System.out.println(line.getStatusCode());
        System.out.println(line.getURL());
    }
}

```

### Grouping the summary by various things

```java
CrawlSummary.byRegisteredDomain(log);
CrawlSummary.byHost(log);
CrawlSummary.byKey(log, item -> item.getCaptureBegan().toString().substring(0, 4)); // by year
```

### Limit top N results

```java
CrawlSummary.build(log).topN(10); // top 10 status codes, mime-types etc
```

### Working with status codes

```java
StatusCodes.describe(404);      // "Not found"
StatusCodes.describe(-4);       // "HTTP timeout"
StatusCodes.isError(-4);        // true
StatusCodes.isServerError(503); // true
```

### Command-line interface

Output a JSON crawl summary grouped by registered domain:

    java org.netpreserve.logtrix.CrawlSummary -g registered-domain crawl.log
    
For more options:

    java org.netpreserve.logtrix.CrawlSummary --help
