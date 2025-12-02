package com.example.downloadmanager.ui;

import com.example.downloadmanager.core.provider.DownloadStatus;
import com.example.downloadmanager.persistence.DownloadEntry;
import com.example.downloadmanager.service.DownloadService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DownloadListCell extends ListCell<DownloadEntry> {

    private final VBox root = new VBox(5);
    private final Label nameLabel = new Label();
    private final Label statusLabel = new Label();
    private final ProgressBar progressBar = new ProgressBar();
    private final Button actionButton = new Button();
    private final Button cancelButton = new Button();
    private final HBox controls = new HBox(10);

    private final DownloadService downloadService;

    public DownloadListCell(DownloadService downloadService) {
        this.downloadService = downloadService;

        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        progressBar.setMaxWidth(Double.MAX_VALUE);

        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.getChildren().addAll(statusLabel, actionButton, cancelButton);

        root.getChildren().addAll(nameLabel, progressBar, controls);

        actionButton.getStyleClass().add("action-button"); // Reuse style
        cancelButton.getStyleClass().add("action-button");
        cancelButton.setStyle("-fx-background-color: #d9534f;"); // Red for cancel
    }

    @Override
    protected void updateItem(DownloadEntry item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(item.getFileName());
            statusLabel.setText(String.format("%s - %s / %s",
                    item.getStatus(),
                    formatBytes(item.getDownloadedBytes()),
                    formatBytes(item.getSizeInBytes())));

            if (item.getSizeInBytes() > 0) {
                progressBar.setProgress((double) item.getDownloadedBytes() / item.getSizeInBytes());
            } else {
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            }

            // Configure buttons based on status
            if (item.getStatus() == DownloadStatus.DOWNLOADING) {
                actionButton.setText("Pause");
                actionButton.setOnAction(e -> downloadService.pauseDownload(item.getId()));
                cancelButton.setDisable(false);
            } else if (item.getStatus() == DownloadStatus.PAUSED) {
                actionButton.setText("Resume");
                actionButton.setOnAction(e -> downloadService.startDownload(item));
                cancelButton.setDisable(false);
            } else if (item.getStatus() == DownloadStatus.COMPLETED) {
                actionButton.setText("Open");
                actionButton.setOnAction(e -> {
                    try {
                        java.awt.Desktop.getDesktop().open(new java.io.File(item.getDestinationPath()));
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                    }
                });
                cancelButton.setDisable(true);
                progressBar.setProgress(1.0);
            } else {
                actionButton.setText("Retry");
                actionButton.setOnAction(e -> downloadService.startDownload(item));
                cancelButton.setDisable(false);
            }

            cancelButton.setText("Cancel");
            cancelButton.setOnAction(e -> downloadService.cancelDownload(item.getId()));

            setGraphic(root);
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
