package com.example.downloadmanager.core.provider;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderDetector {

    private final List<DownloadProviderHandler> providers;

    public ProviderDetector(List<DownloadProviderHandler> providers) {
        this.providers = providers;
    }

    public DownloadProviderHandler detect(String url) {
        // Filter out Generic first, then find first match
        return providers.stream()
                .filter(p -> p.getType() != ProviderType.GENERIC)
                .filter(p -> p.supports(url))
                .findFirst()
                .orElseGet(() -> providers.stream()
                        .filter(p -> p.getType() == ProviderType.GENERIC)
                        .findFirst()
                        .orElseThrow());
    }
}
