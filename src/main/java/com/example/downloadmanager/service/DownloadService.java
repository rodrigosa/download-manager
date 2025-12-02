package com.example.downloadmanager.service;

import com.example.downloadmanager.core.provider.DownloadProviderHandler;
import com.example.downloadmanager.core.provider.DownloadStatus;
import com.example.downloadmanager.core.provider.ProviderDetector;
import com.example.downloadmanager.persistence.DownloadEntry;
import com.example.downloadmanager.persistence.DownloadRepository;

import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DownloadService {

    private final DownloadRepository repository;
    private final ProviderDetector providerDetector;
    private final ExecutorService executorService;
    private final Map<Long, DownloadTask> activeTasks = new ConcurrentHashMap<>();

    public DownloadService(DownloadRepository repository, ProviderDetector providerDetector) {
        this.repository = repository;
        this.providerDetector = providerDetector;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public DownloadEntry addDownload(String url) {
        DownloadProviderHandler provider = providerDetector.detect(url);
        String normalizedUrl = provider.normalizeUrl(url);

        String fileName = normalizedUrl.substring(normalizedUrl.lastIndexOf('/') + 1);
        if (fileName.contains("?")) {
            fileName = fileName.substring(0, fileName.indexOf("?"));
        }
        if (fileName.isEmpty())
            fileName = "downloaded_file";

        // Default download path
        String userHome = System.getProperty("user.home");
        String destination = Paths.get(userHome, "Downloads", fileName).toString();

        DownloadEntry entry = new DownloadEntry();
        entry.setUrl(normalizedUrl);
        entry.setFileName(fileName);
        entry.setDestinationPath(destination);
        entry.setStatus(DownloadStatus.PENDING);
        entry.setProviderType(provider.getType());

        return repository.save(entry);
    }

    public void startDownload(DownloadEntry entry) {
        if (activeTasks.containsKey(entry.getId())) {
            return;
        }
        DownloadTask task = new DownloadTask(entry, repository);
        activeTasks.put(entry.getId(), task);
        executorService.submit(task);
    }

    public void pauseDownload(Long id) {
        DownloadTask task = activeTasks.get(id);
        if (task != null) {
            task.pause();
            activeTasks.remove(id);
        }
    }

    public void cancelDownload(Long id) {
        if (id == null) {
            return;
        }
        DownloadTask task = activeTasks.get(id);
        if (task != null) {
            task.cancel();
            activeTasks.remove(id);
        } else {
            // If not active, just update DB
            repository.findById(id).ifPresent(e -> {
                e.setStatus(DownloadStatus.CANCELLED);
                repository.save(e);
            });
        }
    }

    public List<DownloadEntry> getAllDownloads() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}
