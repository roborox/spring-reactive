package ru.roborox.consul;

import java.util.List;

public interface UrlsProvider {

    List<String> getUrls();

    String next();

}
