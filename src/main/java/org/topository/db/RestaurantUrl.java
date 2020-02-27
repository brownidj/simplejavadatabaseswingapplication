package org.topository.db;

public class RestaurantUrl {

    private String url;
    private String query;
    private String title;
    private String snippet;

    public RestaurantUrl(String query) {
        this.setQuery(query);
    }

    public RestaurantUrl(String url, String query, String title, String snippet) {
        this.setUrl(url);
        this.setQuery(query);
        this.setTitle(title);
        this.setSnippet(snippet);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String toString() {
        return "url='" + url + "'\n" +
                "\ttitle='" + title + "'\n" +
                "\tsnippet='" + snippet + "'\n\n";
    }
}
