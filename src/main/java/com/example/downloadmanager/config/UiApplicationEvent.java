package com.example.downloadmanager.config;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class UiApplicationEvent extends ApplicationEvent {

    public UiApplicationEvent(Stage stage) {
        super(stage);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
