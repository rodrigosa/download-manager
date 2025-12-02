package com.example.downloadmanager.ui;

import com.example.downloadmanager.persistence.DownloadEntry;
import com.example.downloadmanager.service.DownloadService;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainController {

    @FXML
    private TextField urlField;

    @FXML
    private ListView<DownloadEntry> downloadListView;

    @FXML
    private Label statusLabel;

    private final DownloadService downloadService;
    private final ObservableList<DownloadEntry> downloads = FXCollections.observableArrayList();

    public MainController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @FXML
    public void initialize() {
        downloadListView.setItems(downloads);
        downloadListView.setCellFactory(param -> new DownloadListCell(downloadService));

        refreshList();

        // Simple polling for UI updates (in a real app, use properties or events)
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 500_000_000) { // Update every 500ms
                    refreshList();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    @FXML
    public void handleAddDownload() {
        String url = urlField.getText();
        if (url != null && !url.isEmpty()) {
            DownloadEntry entry = downloadService.addDownload(url);
            downloadService.startDownload(entry);
            urlField.clear();
            refreshList();
            statusLabel.setText("Download added: " + entry.getFileName());
        }
    }

    private void refreshList() {
        // This is a naive refresh that re-fetches everything.
        // Ideally we should only update changed items.
        // For MVP, we'll just reload the list to ensure status updates are reflected.
        // To prevent selection loss, we could be smarter here.
        List<DownloadEntry> allDownloads = downloadService.getAllDownloads();

        // Only add new ones or update existing?
        // For simplicity, let's just clear and add if size differs, otherwise assume
        // update in place?
        // Actually, since DownloadEntry objects are re-fetched, they are new instances.
        // We need to update the existing instances in the observable list to keep the
        // UI stable.

        if (downloads.isEmpty()) {
            downloads.addAll(allDownloads);
        } else {
            // Update existing items
            for (int i = 0; i < allDownloads.size(); i++) {
                DownloadEntry newEntry = allDownloads.get(i);
                if (i < downloads.size()) {
                    DownloadEntry current = downloads.get(i);
                    if (current.getId().equals(newEntry.getId())) {
                        // Update fields
                        current.setStatus(newEntry.getStatus());
                        current.setDownloadedBytes(newEntry.getDownloadedBytes());
                        current.setSizeInBytes(newEntry.getSizeInBytes());
                        // Trigger update in ListView
                        downloads.set(i, current);
                    } else {
                        // List structure changed, full reload
                        downloads.setAll(allDownloads);
                        break;
                    }
                } else {
                    downloads.add(newEntry);
                }
            }
        }
    }
}
