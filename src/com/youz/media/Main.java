package com.youz.media;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("media.fxml"));
        stage.setTitle("视频处理工具 V2.0");
        stage.setScene(new Scene(root, 1200, 700));
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
