package com.example.downloadmanager.core.provider;

import org.springframework.stereotype.Component;

@Component
public class GenericProviderHandler implements DownloadProviderHandler {

    @Override
    public boolean supports(String url) {
        return true; // Fallback provider
    }

    @Override
    public ProviderType getType() {
        return ProviderType.GENERIC;
    }

    @Override
    public String normalizeUrl(String url) {
        return url;
    }
}
