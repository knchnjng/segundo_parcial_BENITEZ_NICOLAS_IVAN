package com.example.segundo_parcial_benitez_nicolas_ivan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(
                getClass().getResource("/ProductoView.fxml")
        ));

        Scene scene = new Scene(loader.load(), 900, 500);
        stage.setTitle("Sistema de Ventas - Benítez Nicolás Iván");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}