package com.tb.appmonitor;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main_scene.fxml"));
        stage.setTitle("AppMonitor");
        //初始化Scene
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setScene(scene);
        //设置Stage关闭回调
        final MainController controller = fxmlLoader.getController();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                controller.onStageDestroyed();
            }
        });

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}