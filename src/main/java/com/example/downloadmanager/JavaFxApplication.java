package com.example.downloadmanager;

import com.example.downloadmanager.config.UiApplicationEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(DownloadManagerApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new UiApplicationEvent(stage));
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
