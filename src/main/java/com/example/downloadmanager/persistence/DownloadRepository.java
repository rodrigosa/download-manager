package com.example.downloadmanager.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadRepository extends JpaRepository<DownloadEntry, Long> {
    List<DownloadEntry> findAllByOrderByCreatedAtDesc();
}
