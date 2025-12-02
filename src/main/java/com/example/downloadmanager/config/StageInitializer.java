package com.example.downloadmanager.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<UiApplicationEvent> {

    @Value("classpath:/fxml/dashboard.fxml")
    private Resource dashboardResource;

    private final ApplicationContext applicationContext;

    public StageInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull UiApplicationEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(dashboardResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();

            Stage stage = event.getStage();
            stage.setTitle("Antigravity Download Manager");
            stage.setScene(new Scene(parent, 800, 600));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
