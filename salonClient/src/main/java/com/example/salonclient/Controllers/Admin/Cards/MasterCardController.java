package com.example.salonclient.Controllers.Admin.Cards;

import com.example.salonclient.Controllers.Admin.MastersController;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.ServerAccess;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Optional;

public class MasterCardController {
    private static Master master;
    public void setMaster(Master master) {MasterCardController.master = master;}
    private static MastersController mastersController;

    public void setMastersController(MastersController mastersController) {
        MasterCardController.mastersController = mastersController;}


    @FXML private Label fio;
    @FXML private Label service;
    @FXML private Label experience;
    @FXML private Label hourRate;
    @FXML private Label workingDay;
    @FXML private Label workingTime;
    @FXML private ImageView img;

    @FXML private void toEdit(){
        AddOrEditMasterController.setMaster(master);
        AddOrEditMasterController.setMastersController(mastersController);
        AddOrEditMasterController.setEdit(true);
        mastersController.SetMaster(master,"editMasterCard.fxml");
    }
    @FXML private void toDelete(){
        if (master != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить пользователя?", ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                ServerAccess.sendRequest("/admin/master", "DELETE", master.getId().toString(),
                        response ->
                                Platform.runLater(() -> {
                                    if (response.contains("success")) {
                                        Alert infAlert = new Alert(Alert.AlertType.INFORMATION, "Мастер удален!", ButtonType.OK);
                                        infAlert.show();
                                        mastersController.refreshTable();
                                        mastersController.SetMaster(null, "masterCard.fxml");
                                        //ост?
                                    }
                                }),
                        error -> {
                            Alert warnAlert = new Alert(Alert.AlertType.WARNING, "Ошибка удаления данных: "
                                    + error.getMessage(), ButtonType.OK);
                            warnAlert.show();
                            return;
                        });
            }
        }
    }
    public void setData(Master newMaster){
            setMaster(newMaster);
            fio.setText(master.getFio());
            service.setText("Услуга: " + master.getServiceName());
            experience.setText("Опыт: " + master.getExperience() + " лет");
            hourRate.setText("Часовая ставка: " + master.getHourlyRate().toString() + " руб/час");
            workingDay.setText("Рабочие дни: " + master.getWorkingDay());
            workingTime.setText("Время работы: " + master.getWorkingTime());
            img.setImage(new Image("file:" + master.getAvatarPath()));}
}
