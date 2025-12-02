package com.example.downloadmanager.service;

import com.example.downloadmanager.core.provider.DownloadStatus;
import com.example.downloadmanager.persistence.DownloadEntry;
import com.example.downloadmanager.persistence.DownloadRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
public class DownloadTask implements Runnable {

    @NonNull
    private final DownloadEntry entry;
    private final DownloadRepository repository;
    private volatile boolean running = true;

    public DownloadTask(@NonNull DownloadEntry entry, DownloadRepository repository) {
        this.entry = entry;
        this.repository = repository;
    }

    @Override
    public void run() {
        try {
            updateStatus(DownloadStatus.DOWNLOADING);
            URL url = new URL(entry.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            // Resume logic if supported

            // Check if file exists to resume (omitted for simplicity in MVP, but structure
            // is here)

            connection.connect();

            long fileLength = connection.getContentLengthLong();
            entry.setSizeInBytes(fileLength);
            repository.save(entry);

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(entry.getDestinationPath())) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    if (!running) {
                        updateStatus(DownloadStatus.PAUSED);
                        return;
                    }

                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    entry.setDownloadedBytes(totalBytesRead);

                    // Calculate speed (simple moving average could be better)
                    // For MVP, just update progress occasionally to avoid DB spam
                    // In a real app, we'd update an in-memory model for UI and flush to DB less
                    // often
                }

                updateStatus(DownloadStatus.COMPLETED);
                entry.setCompletedAt(LocalDateTime.now());
                repository.save(entry);
            }
        } catch (IOException e) {
            log.error("Download failed", e);
            updateStatus(DownloadStatus.FAILED);
        }
    }

    public void cancel() {
        running = false;
        updateStatus(DownloadStatus.CANCELLED);
    }

    public void pause() {
        running = false;
        updateStatus(DownloadStatus.PAUSED);
    }

    private void updateStatus(DownloadStatus status) {
        entry.setStatus(status);
        repository.save(entry);
    }
}
