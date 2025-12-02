package com.example.downloadmanager.core.provider;

public interface DownloadProviderHandler {
    boolean supports(String url);

    ProviderType getType();

    String normalizeUrl(String url);
}
