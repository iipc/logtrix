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

### Working with status codes

```java
StatusCodes.describe(404);      // "Not found"
StatusCodes.describe(-4);       // "HTTP timeout"
StatusCodes.isError(-4);        // true
StatusCodes.isServerError(503); // true
```
