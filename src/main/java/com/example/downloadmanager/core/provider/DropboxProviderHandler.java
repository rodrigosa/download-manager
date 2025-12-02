package com.example.downloadmanager.core.provider;

import org.springframework.stereotype.Component;

@Component
public class DropboxProviderHandler implements DownloadProviderHandler {

    @Override
    public boolean supports(String url) {
        return url.contains("dropbox.com");
    }

    @Override
    public ProviderType getType() {
        return ProviderType.DROPBOX;
    }

    @Override
    public String normalizeUrl(String url) {
        if (url.contains("dl=0")) {
            return url.replace("dl=0", "dl=1");
        }
        if (!url.contains("dl=1")) {
            return url + (url.contains("?") ? "&dl=1" : "?dl=1");
        }
        return url;
    }
}
