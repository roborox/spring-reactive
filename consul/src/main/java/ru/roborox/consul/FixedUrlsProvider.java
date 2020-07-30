package ru.roborox.consul;

import java.util.Arrays;
import java.util.List;

public class FixedUrlsProvider implements UrlsProvider {
    private final String url;
    
    public FixedUrlsProvider(String url) {
        this.url = url;
    }

    @Override
    public List<String> getUrls() {
        return Arrays.asList(url);
    }

    @Override
    public String next() {
        return url;
    }

}
