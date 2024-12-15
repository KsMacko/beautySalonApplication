package com.example.salonclient.Controllers.Client;

import com.example.salonclient.Controllers.Client.Cards.MasterCardController;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.GsonProvider;
import com.example.salonclient.Model.JsonConverting;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseMasterController {
    @FXML private FlowPane flow;

    @Setter private static CreateNoteController controller;
    @Setter private static Master chosenMaster;

    @Setter @Getter private static List<MasterCardController> masterCardControllers = new ArrayList<>();
    public void setData(List<Master> masters){
        flow.getChildren().clear();
        try {
            for(Master master:masters) {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("/com/example/salonclient/clientMasterCard.fxml"));
                Pane pane = load.load();
                MasterCardController cardC = load.getController();
                masterCardControllers.add(cardC);
                MasterCardController.setChoose(true);
                cardC.setData(master);
                flow.getChildren().add(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void getRandomMaster(){
        ServerAccess.sendRequest("/master/random?serviceId="+CreateNoteController.getService().getServiceId(), "GET", "", Master.class,
                response ->chosenMaster=response,
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных мастеров: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });
        if(chosenMaster!=null){
            CreateNoteController.setMaster(chosenMaster);
        }
        TransitionControl.style=true;
        MasterCardController.setChoose(false);
        controller.updateCreatingByMaster();
        controller.updateNote();
        TransitionControl.transition("createNote.fxml", false, true);
    }
    public void refreshData() {
        ServerAccess.sendListRequest("/client/master?serviceId="+CreateNoteController.getService().getServiceId(), "GET", "",
                Master[].class,
                response ->
                        Platform.runLater(() -> {
                            System.out.println(response);
                            setData(response);}),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных услуг: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });
    }
    @FXML private void toCancel(){
        TransitionControl.style=true;
        TransitionControl.transition("createNote.fxml", false, true);
    }
    @FXML private void toChoose(){
        if(chosenMaster!=null){
            CreateNoteController.setMaster(chosenMaster);
            TransitionControl.style=true;
            MasterCardController.setChoose(false);
            controller.updateCreatingByMaster();
            controller.updateNote();
            TransitionControl.transition("createNote.fxml", false, true);
        }

    }
    @FXML
    private void initialize(){
        refreshData();
    }
    public static void resetSelection(List<MasterCardController> controllers) {
        for (MasterCardController controller : controllers) { controller.deselect(); } }
}
