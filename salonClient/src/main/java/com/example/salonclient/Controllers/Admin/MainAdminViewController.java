package com.example.salonclient.Controllers.Admin;

import com.example.salonclient.Controllers.Client.MainClientViewController;
import com.example.salonclient.Model.BasicClasses.Client;
import com.example.salonclient.Model.BasicClasses.User;
import com.example.salonclient.Model.TransitionControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import lombok.Setter;

public class MainAdminViewController {
    @FXML VBox vBox;
    @Setter private static MainClientViewController mainClientViewController;
    @FXML public void toOpenSchedule(){
        SetPage("schedule.fxml");}
    @FXML public void toOpenMasters(){
        SetPage("masters.fxml");}
    @FXML public void toOpenClients(){
        SetPage("clients.fxml");}
    @FXML public void toOpenServices(){
        SetPage("services.fxml");}
    @FXML public void toOpenReports(){
        SetPage("summaryMain.fxml");}

    public void SetPage(String page){
        vBox.getChildren().clear();
        try {
            FXMLLoader load = new FXMLLoader();
            load.setLocation(getClass().getResource("/com/example/salonclient/"+page));
            VBox pane = load.load();
            Object controller = load.getController();
            if (controller instanceof LinkedToAdminMainWindow) {
                ((LinkedToAdminMainWindow) controller).setMainAdminViewController(this);
            }
            VBox.setVgrow(pane, Priority.ALWAYS);
            vBox.getChildren().add(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void initialize(){
        SetPage("schedule.fxml");
    }
    @FXML private void toExit(){
        Client.setClient(null);
        User.setUser(null);
        TransitionControl.transition("mainClientView.fxml", true, true);
        mainClientViewController.SetPage("account.fxml");
    }
}
