package org.netpreserve.logtrix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class CrawlLogProcessor {

    @Autowired
    FrontierSummaryProcessor fsp;
    
    @Value("${crawl-log.location}")
    String crawlLogLocation;
    
    boolean ready = false;
    Map<String, HostStats> report = new HashMap<>();

    List<String> hosts;
    
    @Async
    public void run() {
        log.info("Reading {}", crawlLogLocation);
        int itemsRead = 0;
        try (CrawlLogIterator cli = new CrawlLogIterator(crawlLogLocation)) {
        	while (cli.hasNext()) {
        		CrawlDataItem item = cli.next();
        		String host = getHostname(item.getURL());
        		HostStats stats = report.get(host);
        		if (stats == null) {
        			stats = new HostStats(host);
        			report.put(host, stats);
        		}
        		stats.addCrawlDataItem(item);
        		itemsRead++;
        	}
        } catch (IOException e) {
        	log.error("Failed to read crawl log {}", crawlLogLocation, e);
        } 
        
        prepareHostList();
        
        ready = true;
        log.info("Crawl log report ready. Processed {} items. {} hosts", itemsRead, hosts.size());
	}
    
    public String getHostname(String url) {
    	if (url.startsWith("dns:")) {
    		return url.substring(4);
    	}
    	int start = url.indexOf("//")+2; //Plus to to skip over the //
    	int end = url.indexOf('/', start);
    	return url.substring(start, end);
    }
    
    private void prepareHostList() {
    	while (!fsp.isReady()) {
			log.debug("Waiting on frontier report");
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
    	}
    	Map<String, QueueStats> frontierReport = fsp.getReport();
    	List<String> tmpHosts = new ArrayList<>(frontierReport.size());

    	
    	for (String host : frontierReport.keySet()) {
    		// Get all hosts on the frontier active list with at least X items crawled.
    		if ( (report.containsKey(host) && report.get(host).items.size()>1) 
    				|| (report.containsKey("www."+host) && report.get("www."+host).items.size()>1) ) {
    			tmpHosts.add(Util.hostnameToSurt(host));
    		}
    	}
    	
    	Collections.sort(tmpHosts);
    	this.hosts = new ArrayList<>(tmpHosts.size());
    	for (String s : tmpHosts) {
    		this.hosts.add(Util.surtToRegular(s));
    	}
    }


    
	public boolean isReady() {
		return ready;
	}

	public Map<String, HostStats> getReport() {
		return report;
	}

	public List<String> getHosts() {
		return hosts;
	}
    
}