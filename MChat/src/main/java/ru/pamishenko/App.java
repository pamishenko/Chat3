package ru.pamishenko;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;



/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        boolean authorize = false;


            stage.setTitle("MChat");
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            Image image = new Image(iconStream);
            stage.getIcons().add(image);

            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/homeScene.fxml");
            loader.setLocation(xmlUrl);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();

    }

    public static void main(String[] args) {
        App.launch();


    }



}