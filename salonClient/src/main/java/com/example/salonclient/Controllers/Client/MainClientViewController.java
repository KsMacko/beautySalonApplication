package com.example.salonclient.Controllers.Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainClientViewController {
//
    @FXML VBox vBox;

    @FXML public void toOpenContacts(){
        SetPage("contacts.fxml");}
    @FXML public void toOpenMain(){
        SetPage("mainPage.fxml");}
    @FXML public void toOpenAbout(){
        SetPage("about.fxml");}
    @FXML public void toOpenServices(){
        SetPage("catalog.fxml");}
    @FXML public void toOpenMasters(){
         SetPage("clientMasters.fxml");}
    @FXML public void toOpenAccount(){
        SetPage("account.fxml");}

    public void SetPage(String page){
        vBox.getChildren().clear();
            try {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("/com/example/salonclient/"+page));
                ScrollPane pane = load.load();
                Object controller = load.getController();
                if (controller instanceof LinkedToMainWindow) {
                    ((LinkedToMainWindow) controller).setMainClientViewController(this);
                }
                VBox.setVgrow(pane, Priority.ALWAYS);
                vBox.getChildren().add(pane);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    @FXML private void initialize(){
        SetPage("mainPage.fxml");
    }
}
