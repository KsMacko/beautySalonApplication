package com.example.salonclient.Controllers.Admin.Cards;

import com.example.salonclient.Controllers.Admin.ScheduleController;
import com.example.salonclient.Model.BasicClasses.Schedule;
import com.example.salonclient.Model.ServerAccess;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.Setter;

import java.time.Duration;
import java.util.Optional;

public class NoteController {
    @FXML private Label name;
    @FXML private Label telephone;
    @FXML private Label duration;
    @FXML private Label service;
    @FXML private Label price;
    private Schedule schedule;
    @Setter private ScheduleController scheduleController;

    public void setData(Schedule schedule){
        this.schedule=schedule;
        name.setText("Клиент: "+schedule.getClientName());
        telephone.setText("Телефон: "+schedule.getTelephone());
        duration.setText("Длительность: "+schedule.getDuration().getHour()+"ч "+
                schedule.getDuration().getMinute()+"мин");
        price.setText("Цена: "+schedule.getPrice()+" BYN");
        service.setText("Услуга: "+schedule.getServiceName());
    }
    @FXML private void toDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить запись?", ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            ServerAccess.sendRequest("/admin/note/scheduleId?=" + schedule.getScheduleId(), "DELETE", "",
                    response -> Platform.runLater(() -> {
                        if (response.contains("success")) {
                            scheduleController.refreshData();
                        }
                    }),
                    error -> {
                        Alert nalert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных записей: " + error.getMessage(), ButtonType.OK);
                        nalert.show();
                    }
            );
        }
    }
}
