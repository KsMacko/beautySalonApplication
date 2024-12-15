package com.example.salonclient.Controllers.Client;

import com.example.salonclient.Controllers.Client.Cards.MasterCardController;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.ServerAccess;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.List;

public class ClientMastersController{
    @FXML
    private FlowPane flow;

    public void setData(List<Master> masters){
        flow.getChildren().clear();
        try {
            for(Master master:masters) {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("/com/example/salonclient/clientMasterCard.fxml"));
                Pane pane = load.load();
                MasterCardController cardC = load.getController();
                cardC.setData(master);
                flow.getChildren().add(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshData() {
        ServerAccess.sendListRequest("/client/master/all", "GET", "", Master[].class,
                response ->
                        Platform.runLater(() -> setData(response)),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных мастеров: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });
    }
    public void refreshData(List<Master> temporaryData) {
        setData(temporaryData); }
    @FXML private void initialize(){
        refreshData();
    }
}
