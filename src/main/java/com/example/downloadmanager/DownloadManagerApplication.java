package com.example.downloadmanager;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DownloadManagerApplication {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}
