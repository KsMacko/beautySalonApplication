package com.example.salonclient.Controllers.Client;

import com.example.salonclient.Model.BasicClasses.Client;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.BasicClasses.Service;
import com.example.salonclient.Model.JsonConverting;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CreateNoteController {
    @FXML private Label name;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label curMaster;
    @FXML private Label price;
    @FXML private Label duration;

    @FXML private Label chooseDate;
    @FXML private AnchorPane anchor;
    @FXML private AnchorPane legend;
    @FXML private FlowPane timePicker;

    @Getter @Setter private static Service service;
    @Setter private static Master master;
    private static String selectedTime;
    private static LocalDate selectedDate;

    private static MainClientViewController mainClientViewController;
    public static void setMainClientViewController(MainClientViewController mainClientViewController) {
        CreateNoteController.mainClientViewController = mainClientViewController;}

    @FXML private void toCancel(){
        TransitionControl.style=false;
        TransitionControl.transition("mainClientView.fxml", true, true);
        mainClientViewController.SetPage("catalog.fxml");
    }

    public void setCalendar() {
        anchor.getChildren().clear();
        DatePicker datePicker = new DatePicker();
        LocalDate maxDate = LocalDate.now().plusMonths(2);
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
                if (empty || item == null) {
                    setDisable(true);
                    setStyle("");
                } else {
                    if (item.isBefore(LocalDate.now())|| item.isAfter(maxDate)) {
                        setDisable(true);
                        setStyle("-fx-text-fill: #7b7878;");
                    } else if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        setDisable(true);
                        setStyle("-fx-text-fill: #7b7878;");
                    } else {
                        checkAllConditions(item, isWorking -> {
                            if (!isWorking) {
                                setDisable(true);
                                setStyle("-fx-background-color: #d1d1d1;");
                            } else {
                                setStyle("-fx-background-color: transparent;");
                            }
                            if (item.equals(selectedDate)) setStyle("-fx-background-radius: 10; -fx-border-radius: 10; " +
                                    "-fx-background-color: #c0a57b;");
                        });
                    }

                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
        datePicker.setOnAction(e -> {
            selectedDate = datePicker.getValue();
            onDateSelected(datePicker.getValue()); });

        DatePickerSkin datePickerSkin = new DatePickerSkin(datePicker);
        Node popupContent = datePickerSkin.getPopupContent();
        AnchorPane.setTopAnchor(popupContent, 0.0);
        AnchorPane.setBottomAnchor(popupContent, 0.0);
        AnchorPane.setLeftAnchor(popupContent, 0.0);
        AnchorPane.setRightAnchor(popupContent, 0.0);
        anchor.getChildren().add(popupContent);
    }

    private void checkAllConditions(LocalDate date, Consumer<Boolean> callback) {
        isWorkingDay(date, isWorking -> {
            System.out.println("working? "+isWorking);
            if (!isWorking) {
                callback.accept(false);
            } else {
                checkAllBooked(date, list -> {
                    System.out.println("All booked? "+list.size());
                    //////////////
                    callback.accept(!list.isEmpty());
                }); }
        });
    }
    private void isWorkingDay(LocalDate date, Consumer<Boolean> callback) {
        ServerAccess.sendRequest("/client/master/available?id="+master.getId()+"&date="+date.toString(),
                "GET", "",
                response ->
                        Platform.runLater(() -> {
                            boolean isWorking = Boolean.parseBoolean(response);
                            callback.accept(isWorking);
                        }),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных свободного времени для записей: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });
    }
    private void checkAllBooked(LocalDate date, Consumer<List<LocalTime>> callback) {
        ServerAccess.sendListRequest("/client/availableTimes?id="+master.getId()+"&date="+date.toString(),
                "GET", "",LocalTime[].class,
                response ->
                        Platform.runLater(() -> {
                            callback.accept(response);
                        }),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных свободного времени для записей: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });
    }

    public Button createStyledButton(String text) {
        Button timeButton = new Button(text);
        timeButton.setMinHeight(20.0);
        timeButton.setMinWidth(60.0);
        timeButton.setStyle("-fx-background-color: #4a2d77; -fx-background-radius: 10; -fx-border-radius: 10;");
        timeButton.setTextFill(Color.WHITE);
        timeButton.setFont(new Font(12.0));
        timeButton.setOnAction(e -> {
            timePicker.getChildren().forEach(node -> {
                if (node instanceof Button button) {
                    button.setStyle("-fx-background-color: #4a2d77; -fx-background-radius: 10; -fx-border-radius: 10;");
                    button.setTextFill(Color.WHITE);
                }
            });
            timeButton.setStyle("-fx-background-color: white; -fx-border-color: #4a3d77; -fx-background-radius: 10; -fx-border-radius: 10;");
            timeButton.setTextFill(Color.valueOf("#4a3d77"));
            selectedTime = timeButton.getText();
            timeLabel.setText(selectedTime);
            updateNote();
        });
        return timeButton;
    }

    private void onDateSelected(LocalDate date) {
        selectedDate = date;
        legend.setVisible(false);
        checkAllBooked(date, list -> {
            Platform.runLater(() -> {
                timePicker.getChildren().clear();
                if (!list.isEmpty()) {
                    for (LocalTime time : list) {
                        Button timeButton = createStyledButton(time.toString());
                        timePicker.getChildren().add(timeButton);
                    }
                }
                timePicker.setVisible(true);
            });
        });
        updateNote();
    }

    public void updateNote(){
        if (master != null) { curMaster.setText(master.getFio()); }
        if(selectedDate!=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedDate = selectedDate.format(formatter);
            dateLabel.setText("Дата: " + formattedDate);}
        if(selectedTime!=null) timeLabel.setText("Время: "+selectedTime);
    }
    public void updateCreatingByMaster(){
        selectedDate = null;
        selectedTime = null;
        timePicker.getChildren().clear();
        legend.setVisible(true);
        chooseDate.setVisible(true);
        setCalendar();
    }
    @FXML private void ChooseMaster(){
        ChooseMasterController.setController(this);
        TransitionControl.style=true;
        TransitionControl.transition("masterChooser.fxml", false, false);
    }
    @FXML private void toCreateNote(){
        if(master == null){
            showAlert("Мастер не выбран!");
            return;
        }
        if(selectedDate==null){
            showAlert("Время не выбрано!");
            return;
        }
        if(selectedTime==null){
            showAlert("Время не выбрано!");
            return;
        }
        ServerAccess.sendRequest("/client/note?id="+master.getId()+"&clientLogin="+Client.getUser().getLogin()
                        +"&date="+selectedDate+"&time="+selectedTime, "POST", "",
                response ->
                        Platform.runLater(() -> {
                            if(response.contains("success")){
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Запись успешно добавлена!", ButtonType.OK);
                                alert.showAndWait();
                                TransitionControl.style=false;
                                TransitionControl.transition("mainClientView.fxml", true, true);
                                mainClientViewController.SetPage("catalog.fxml");
                            }
                        }),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка добавления записи: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });
    }
    @FXML private void initialize(){
        chooseDate.setVisible(false);
        legend.setVisible(false);
        name.setText(service.getName());
        curMaster.setText("");
        dateLabel.setText("Дата: ");
        timeLabel.setText("Время: ");
        price.setText("Цена: "+service.getPrice().toString()+" BYN");
        duration.setText("Продолжительность: "+service.getDuration().getHour()+"ч "+service.getDuration().getMinute()+"мин");

    }
    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}
