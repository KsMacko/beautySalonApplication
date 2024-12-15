package com.example.salonclient.Controllers.Admin;

import com.example.salonclient.Controllers.Admin.Cards.NoteController;
import com.example.salonclient.Model.BasicClasses.Schedule;
import com.example.salonclient.Model.ServerAccess;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleController {
    @FXML private DatePicker dateChooser;
    @FXML private Label servicedClients;
    @FXML private Label income;
    @FXML private GridPane dataGrid;
    @FXML private GridPane back;
    public static Schedule schedule;


    private Map<String, Integer> masterColumnIndexMap = new HashMap<>();

    @FXML
    private void initialize() {
        dataGrid.setGridLinesVisible(true);
        setupGridTimeLabels();
        dateChooser.setValue(LocalDate.now());
        refreshData();
        dateChooser.setOnAction(e -> refreshData());
    }

    private void setData(List<Schedule> schedules) {
        dataGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) != 0);
        dataGrid.setGridLinesVisible(true);
        setupGridTimeLabels();
        try {
            for (Schedule schedule : schedules) {
                ScheduleController.schedule=schedule;
                int columnIndex = masterColumnIndexMap.computeIfAbsent(schedule.getFio(), k -> masterColumnIndexMap.size() + 1);
                Label masterLabel = new Label(schedule.getFio());
                Label l =new Label("");
                l.setMinWidth(200);
                masterLabel.setStyle("-fx-font-size: 13; -fx-min-width: 200px; -fx-wrap-text: true; -fx-padding: 0 0 0 5");
                back.add(l, columnIndex, 0);
                dataGrid.add(masterLabel, columnIndex, 0);

                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("/com/example/salonclient/adminNote.fxml"));
                HBox pane = load.load();
                NoteController cardC = load.getController();
                cardC.setData(schedule);
                int rowIndex = calculateRowIndex(schedule.getTime());
                int rowSpan = calculateRowSpan(schedule.getTime(), schedule.getDuration());

                dataGrid.add(pane, columnIndex, rowIndex, 1, rowSpan);
            }
            servicedClients.setText("Обслуженные клиенты: "+schedules.size());
            income.setText("Прибыль: "+schedules.stream() .map(Schedule::getPrice) .reduce(BigDecimal.ZERO, BigDecimal::add)+" BYN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int calculateRowIndex(LocalTime startTime) {
        int startHour = startTime.getHour() - 10;
        int startMinute = startTime.getMinute() / 15;
        return startHour * 4 + startMinute + 1;
    }

    private int calculateRowSpan(LocalTime startTime, LocalTime duration) {
        return (int) Duration.between(startTime, startTime.plusMinutes(duration.getHour() * 60 + duration.getMinute())).toMinutes() / 15 +2;
    }

    private void setupGridTimeLabels() {
        dataGrid.getChildren().clear();
        masterColumnIndexMap.clear();
        LocalTime time = LocalTime.of(10, 0);
        for (int i = 0; i < 41; i++) {
            Label timeLabel = new Label(time.toString());
            if (time.getMinute() == 0) {
                timeLabel.setStyle("-fx-font-size: 16;-fx-padding: 0 0 0 4");
            } else {
                timeLabel.setStyle("-fx-font-size: 13; -fx-padding: 0 0 0 10");
            }
            timeLabel.setMinHeight(40);
            dataGrid.add(timeLabel, 0, i + 1);
            time = time.plusMinutes(15);
        }

        RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(40);
            rowConstraints.setPrefHeight(40);
            dataGrid.getRowConstraints().add(rowConstraints);
    }

    public void refreshData() {
        ServerAccess.sendListRequest("/admin/note/schedule?date=" + dateChooser.getValue(), "GET", "", Schedule[].class,
                response -> Platform.runLater(() -> setData(response)),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных записей: " + error.getMessage(), ButtonType.OK);
                    alert.show();
                }
        );
    }
}
