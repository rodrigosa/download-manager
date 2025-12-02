package com.example.downloadmanager.persistence;

import com.example.downloadmanager.core.provider.DownloadStatus;
import com.example.downloadmanager.core.provider.ProviderType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class DownloadEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2048)
    private String url;

    private String fileName;
    private String destinationPath;

    @Enumerated(EnumType.STRING)
    private DownloadStatus status;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    private long sizeInBytes;
    private long downloadedBytes;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
