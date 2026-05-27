import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public static class MultithreadedWebCrawler {
    // Store the hostname from startUrl to filter URLs
    private String hostName;

    //5 threads to simulate multithreading
    ExecutorService executor = Executors.newFixedThreadPool(5);

    //checks if url was visited . ensures we dont go into loops
    //we use this since we dont really have a concurrent-hashset
    private ConcurrentHashMap<String, Boolean> urlHashMap = new ConcurrentHashMap<>();

    // Counter to track URLs to be processed
    private AtomicInteger numOfUrlsToParse = new AtomicInteger(0);

    // Reference to the HtmlParser
    private HtmlParser htmlParser;

    class Task implements Runnable {
        private String url;

        Task(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            //check for all urls on the current page
            for (String extractedUrl : htmlParser.getUrls(url)) {
                // Extract hostname from URL
                String curHostName = extractedUrl.split("/")[2];
                // Check if URL has same hostname and hasn't been visited
                if (curHostName.equals(hostName) && urlHashMap.putIfAbsent(extractedUrl, true) == null) {
                    // Increment counter for active tasks
                    numOfUrlsToParse.addAndGet(1);
                    // Submit new task to process this URL
                    executor.submit(new Task(extractedUrl));
                }
            }
            // Decrement counter when task is complete
            numOfUrlsToParse.addAndGet(-1);
        }
    }

    // startUrl = "http://news.yahoo.com/news/topics/"
    public List crawl(String startUrl, HtmlParser htmlParser) {
        // Extract hostname from startUrl
        // startUrl Split Array = ["http:", "", "news.yahoo.com", "news", "topics", ""]
        // hostname = "news.yahoo.com"
        hostName = startUrl.split("/")[2];
        this.htmlParser = htmlParser;

        // Mark startUrl as visited
        urlHashMap.put(startUrl, true);

        // Initialize counter and submit first task
        numOfUrlsToParse.addAndGet(1);
        executor.submit(new Task(startUrl));

        // Wait until all URLs have been processed
        while (numOfUrlsToParse.get() > 0) {
            try {
                // Sleep to avoid busy waiting
                Thread.sleep(80);
            } catch (Exception e) {
                // Handle exceptions
            }
        }

        // Shutdown the thread pool
        executor.shutdown();

        // Return all discovered URLs
        return new ArrayList<>(urlHashMap.keySet());
    }
}

static class HtmlParser {
    List<String> urls;
    int[][] edges;
    HtmlParser(List<String> urls,int[][] edges) {
        this.urls = urls;
        this.edges = edges;
    }

    List<String> getUrls(String url) {
        List<String> res = new ArrayList<>();
        int idx = urls.indexOf(url);
        //iterate over all edges
        for(int[] edge : edges) {
            if(edge[0] == idx) {
                res.add(urls.get(edge[1]));
            }
        }
        return res;
    }
}

void main(){
    //initiate html parse
    HtmlParser parser = new HtmlParser(
            List.of("http://news.yahoo.com",
                    "http://news.yahoo.com/news",
                    "http://news.yahoo.com/news/topics/",
                    "http://news.google.com"),
            new int[][]{{0,2},{2,1},{3,2},{3,1},{3,0}}
    );

    MultithreadedWebCrawler crawler = new MultithreadedWebCrawler();
    List<String> res =crawler.crawl("http://news.yahoo.com", parser);
    res.forEach(System.out::println);
}
