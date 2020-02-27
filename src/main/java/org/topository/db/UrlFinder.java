package org.topository.db;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topository.restaurant.Restaurant;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UrlFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlFinder.class);

    private static final UrlFinder INSTANCE = new UrlFinder();
    private final static AtomicReference<Throwable> throwable = new AtomicReference<Throwable>(null);
    private final static AtomicBoolean isDone = new AtomicBoolean(false);

    private final static String DUCKDUCKGO_SEARCH_URL = "https://duckduckgo.com/html/?q=";
    private final static String DUCKDUCKGO_FORMAT_URL = "&format=xml";
    private final static int max = 4;
    private String results;
    private List<RestaurantUrl> restaurantUrls;
    private Elements elements;

    private UrlFinder() {
    }

    public static void main(String[] args) throws Exception {
        UrlFinder urlFinder = new UrlFinder();
        Restaurant restaurant = new Restaurant();
        restaurant.setName("African Village Centre Restaurant");
        restaurant.setCity("Adelaide");

        urlFinder.findUrl(restaurant);

    }

    public static UrlFinder getInstance() {
        return UrlFinder.INSTANCE;
    }

    public String findUrl(Restaurant restaurant) throws InterruptedException {
        SwingWorker<Boolean, RestaurantUrl> worker = this.getUrlSwingWorker(this, restaurant);
        worker.execute();

        int i = 10;
        int count = 0;

        while (!isDone.get() && count++ < i) {
            UrlFinder.LOGGER.info("Waiting...");
            Thread.sleep(1000);
        }
        if (throwable.get() instanceof InterruptedException) {
            throw new RuntimeException("UrlFinder.findUrl failed");
        } else {
            UrlFinder.LOGGER.info("Results = " + getResults());
        }
        return this.getResults();
    }

    public String getResults() {
        StringBuilder builder = new StringBuilder("\n");
        for (RestaurantUrl restaurantUrl : restaurantUrls) {
            builder.append(restaurantUrl.toString());
        }
        results = builder.toString();
        return results;
    }

    private SwingWorker<Boolean, RestaurantUrl> getUrlSwingWorker(UrlFinder urlFinder, Restaurant restaurant) {
        return new SwingWorker<Boolean, RestaurantUrl>() {

            @Override
            protected Boolean doInBackground() {
                UrlFinder.LOGGER.info("Running SwingWorker");
                restaurantUrls = urlFinder.getRestaurantUrls(restaurant);
                while (restaurantUrls.equals(null)) {
                    UrlFinder.LOGGER.info("Sleeping... ");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (RestaurantUrl restaurantUrl : restaurantUrls) {
                    publish(restaurantUrl);
                }
                return !restaurantUrls.isEmpty();
            }

            @Override
            protected void done() {
                isDone.set(true);
                UrlFinder.LOGGER.info("Finished with status " + isDone.get());
            }
        };
    }

    public List<RestaurantUrl> getRestaurantUrls(Restaurant restaurant) {
        UrlFinder.LOGGER.info("Running query for " + restaurant.toString());
//        StringBuilder builder = new StringBuilder("\n");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UrlFinder.LOGGER.info("Running query");
                restaurantUrls = getDuckDuckGoSearchResults(restaurant);
//                if (!restaurantUrls.isEmpty()) {
//                    for (int i = 0; i < elements.size(); i++) {
//                        String url = restaurantUrls.get(i).getUrl();
//                        if (url != null) {
//                            builder.append(url + "\n");
//                        }
//                    }
//                    UrlFinder.LOGGER.debug("Builder string = " + builder.toString());
//                } else {
//                    builder.append("No URLs found");
//                }
            }
        });
        return restaurantUrls;
    }


    private List<RestaurantUrl> getDuckDuckGoSearchResults(Restaurant restaurant) {
        Document doc = null;
        restaurantUrls = new ArrayList<>();
        String query = this.getQuery(restaurant);

        try {
            String searchUrl = DUCKDUCKGO_SEARCH_URL + query + DUCKDUCKGO_FORMAT_URL;
            UrlFinder.LOGGER.info("The URL is  = " + searchUrl);
            doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/605.1.15 Version/13.1 Safari/605.1.15")
                    .maxBodySize(15000).get();
            elements = doc.getElementById("links").getElementsByClass("results_links");
            UrlFinder.LOGGER.info("Elements size = " + elements.size());

            for (Element e : elements) {
                Element element = e.getElementsByClass("links_main").first().getElementsByTag("a").first();
                RestaurantUrl restaurantUrl = new RestaurantUrl(query);
                if (element != null) {
                    if (element.attr("href") != null || !element.attr("href").isEmpty()) {
                        restaurantUrl.setUrl(element.attr("href"));
                    }
                    if (!element.text().isEmpty()) {
                        restaurantUrl.setTitle(element.text());
                    }
                    if (e.getElementsByClass("result__snippet").first() != null) {
                        restaurantUrl.setSnippet(e.getElementsByClass("result__snippet").first().text());
                    }
                    restaurantUrls.add(restaurantUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return restaurantUrls;
    }

    private String getQuery(Restaurant restaurant) {
        List<String> details = new ArrayList<>();
        details.addAll(Arrays.asList(this.getStringArray(restaurant.getName())));
        details.addAll(Arrays.asList(this.getStringArray(restaurant.getCity())));
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (; i < details.size() - 1; i++) {
            stringBuilder.append(details.get(i));
            stringBuilder.append('+');
        }
        String query = stringBuilder.append(details.get(i)).toString();
        UrlFinder.LOGGER.debug("The query is for " + query);
        return query;
    }

    private String[] getStringArray(String s) {
        return s.split("[[ ]*|[,]*|[/]*|[!]*|[?]*|[+]]");
    }


}
