package ru.pamishenko.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class Controller {

    public Controller(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/homeScene.fxml"));
        Controller controller = loader.getController();
    }

    @FXML
    public void exitButtonClicked(ActionEvent actionEvent) {
        System.out.println("кнопка закрыть нажата");
    }


}
