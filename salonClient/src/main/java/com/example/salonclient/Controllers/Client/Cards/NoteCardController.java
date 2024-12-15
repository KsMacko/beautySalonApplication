package com.example.salonclient.Controllers.Client.Cards;

import com.example.salonclient.Controllers.Client.AccountController;
import com.example.salonclient.Model.BasicClasses.Schedule;
import com.example.salonclient.Model.ServerAccess;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class NoteCardController {
    private Schedule curSchedule;
    @FXML private Label name;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label curMaster;
    @FXML private Label price;
    @FXML private Label duration;
    private static AccountController accountController;
    public void setAccountController(AccountController accountController) {
        NoteCardController.accountController = accountController;}
    @FXML private void toDelete(){
        if (curSchedule != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить запись?", ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                ServerAccess.sendRequest("/client/note?scheduleId="+curSchedule.getScheduleId(), "DELETE", "",
                        response ->
                                Platform.runLater(() -> {
                                    if (response.contains("success")) {
                                        Platform.runLater(()->{
                                                Alert infAlert = new Alert(Alert.AlertType.INFORMATION, "Запись удалена!", ButtonType.OK);
                                        infAlert.show();
                                        accountController.refreshData();
                                        });
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

    public void setData(Schedule schedule){
        curSchedule = schedule;
        name.setText(schedule.getServiceName());
        timeLabel.setText(schedule.getTime().toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = schedule.getDate().format(formatter);
        dateLabel.setText(formattedDate);
        curMaster.setText(schedule.getFio());
        price.setText("Стоимость: "+schedule.getPrice().toString()+" BYN");
        duration.setText("Продолжительность: "+schedule.getDuration().getHour()+"ч "+
                schedule.getDuration().getMinute()+"мин");
    }
}
