package com.example.salonclient.Controllers.Admin.Cards;

import com.example.salonclient.Controllers.Admin.MainAdminViewController;
import com.example.salonclient.Controllers.Admin.MastersController;
import com.example.salonclient.Controllers.Admin.ServiceInfoOrEditController;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.BasicClasses.Service;
import com.example.salonclient.Model.GsonProvider;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

public class AddOrEditMasterController {
    private static MastersController mastersController;
    public static void setMastersController(MastersController mastersController) {
        AddOrEditMasterController.mastersController = mastersController;}
    private static MainAdminViewController mainAdminViewController;

    public static void setMainAdminViewController(MainAdminViewController mainAdminViewController) {
        AddOrEditMasterController.mainAdminViewController = mainAdminViewController;}
    private static Boolean edit=false;
    public static void setEdit(Boolean edit) {AddOrEditMasterController.edit = edit;}
    private static Master master;
    public static void setMaster(Master master) {AddOrEditMasterController.master = master;}

    @FXML private ImageView img;
    @FXML private TextField fio;
    @FXML private ComboBox<Service> serviceChoice;
    @FXML private TextField exp;
    @FXML private TextField hourRate;
    @FXML private TextField imgPath;

    @FXML private CheckBox Mn;
    @FXML private CheckBox Ts;
    @FXML private CheckBox Wd;
    @FXML private CheckBox Th;
    @FXML private CheckBox Fr;

    @FXML private ComboBox<String> timePickerStart;
    @FXML private ComboBox<String> timePickerEnd;

    @FXML private void toCancel(){
        TransitionControl.style=false;
        TransitionControl.transition("mainAdminView.fxml", true, true);
        mainAdminViewController.SetPage("masters.fxml");
    }
    @FXML private void toSave(){
        Master master = createMaster();
        if (master == null) {
            return;}
        String jsonInputString = GsonProvider.createGson().toJson(master);
        ServerAccess.sendRequest("/admin/master", "POST", jsonInputString,
                response -> {
                    Platform.runLater(() -> {
                        if(response.contains("success")){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Мастер добавлен!", ButtonType.OK);
                        alert.show();
                        mastersController.refreshTable();
                        TransitionControl.style=false;
                        TransitionControl.transition("mainAdminView.fxml", true, true);}
                        mainAdminViewController.SetPage("masters.fxml");
                    });
                },
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных услуг: " + error, ButtonType.OK);
                    alert.show();
                });
    }
    private Master createMaster(){
        String fioValue = fio.getText();
        String expValue = exp.getText();
        String hourRateValue = hourRate.getText();
        String imgPathValue = imgPath.getText();
        List<String> selectedDays = getSelectedDays();
        String startTimeValue = timePickerStart.getValue();
        String endTimeValue = timePickerEnd.getValue();
        Service selectedService = serviceChoice.getValue();
        if (fioValue.isEmpty() || expValue.isEmpty() || hourRateValue.isEmpty() || imgPathValue.isEmpty()
                || selectedDays.isEmpty() || selectedService == null || startTimeValue==null ||
                endTimeValue==null|| startTimeValue.isEmpty()||
                endTimeValue.isEmpty()) {
            showWarningAlert("Все поля должны быть заполнены!");
            return null; }
        if (!fioValue.matches("^[А-Яа-я\\s]+$") || fioValue.split(" ").length != 3) {
            showWarningAlert("ФИО должно состоять из трех русских слов, разделенных пробелами!");
            return null; }
        int experience; try {
            experience = Integer.parseInt(expValue);
            if (experience < 0) { showWarningAlert("Опыт работы не может быть отрицательным числом!");
                return null; }
        } catch (NumberFormatException e) {
            showWarningAlert("Опыт работы должен быть целым числом!");
            return null; }
        BigDecimal hourlyRate;
        try { hourlyRate = new BigDecimal(hourRateValue).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            try { hourlyRate = new BigDecimal(Integer.parseInt(hourRateValue)).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e2) {
                showWarningAlert("Часовая ставка должна быть числом с плавающей запятой до 2 знаков или целым числом!");
                return null; }
        }
        if (hourlyRate.compareTo(BigDecimal.ZERO) < 0) {
            showWarningAlert("Часовая ставка не может быть отрицательным числом!");
            return null;
        }
        LocalTime startTime = LocalTime.parse(startTimeValue);
        LocalTime endTime = LocalTime.parse(endTimeValue);
        if (startTime.isBefore(LocalTime.of(10, 0)) ||
                endTime.isAfter(LocalTime.of(20, 0)) ||
                !startTime.isBefore(endTime)) {
            showWarningAlert("Время должно быть с 10:00 до 20:00, начальное время должно быть меньше конечного!");
            return null; }
        Master master = new Master();
        master.setFio(fioValue);
        master.setExperience(experience);
        master.setHourlyRate(hourlyRate);
        master.setAvatarPath(imgPathValue);
        master.setServiceName(selectedService.getName());
        master.setWorkingDay(String.join(",", selectedDays));
        master.setStartTime(startTime); master.setEndTime(endTime);
        return master;
    }
    @FXML public void toFindTheImage(){
        ServiceInfoOrEditController.FindImage(imgPath, img);
    }
    public ArrayList<String> getSelectedDays() {
        List<CheckBox> days = Arrays.asList(Mn, Ts, Wd, Th, Fr);
        ArrayList<String> selectedDays = new ArrayList<>();
        for (CheckBox day : days) {
            if (day.isSelected()) {
                selectedDays.add(day.getText());}}
        return selectedDays;
    }

    @FXML private void toUpdateMaster(){
        Master master = createMaster();
        if (master == null) {
            return;}
        String jsonInputString = GsonProvider.createGson().toJson(master);
        ServerAccess.sendRequest("/admin/master", "PUT", jsonInputString,
                response -> {
                    Platform.runLater(() -> {
                        if(response.contains("success")){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Мастер сохранен!", ButtonType.OK);
                            alert.show();
                            edit=false;
                            mastersController.refreshTable();
                            mastersController.SetMaster(master, "masterCard.fxml");}
                    });
                },
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка при сохранении изменений: " + error, ButtonType.OK);
                    alert.show();
                });
    }
    @FXML private void toDeny(){
        edit=false;
        mastersController.SetMaster(master, "masterCard.fxml");
    }
    @FXML private void initialize(){
        if(serviceChoice!=null) {
            ServerAccess.sendListRequest("/admin/service", "GET", "", Service[].class,
                    response -> {
                        Platform.runLater(() -> {
                            serviceChoice.getItems().clear();
                            for (Service service : response) {
                                serviceChoice.getItems().add(service);
                            }
                            if(edit&& master!=null){
                                for (Service service : serviceChoice.getItems()) {
                                    if (service.getName().equals(master.getServiceName())) {
                                        serviceChoice.setValue(service);
                                        break; } }
                            }
                        });
                    },
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка получения данных услуг: " + error, ButtonType.OK);
                        alert.show();
                    });
        }
        MastersController.setWorkingTimeValues(timePickerStart, timePickerEnd);
        timePickerEnd.setOnAction(event -> {
            String selectedEnd = timePickerEnd.getValue();
            if(selectedEnd!=null) {
                int endHour = Integer.parseInt(selectedEnd.split(":")[0]);
                List<String> filteredStartTimes = IntStream.rangeClosed(10, endHour - 1)
                        .mapToObj(hour -> String.format("%02d:00", hour))
                        .toList();
                String selectedStart = timePickerStart.getValue();
                timePickerStart.getItems().setAll(filteredStartTimes);
                if (filteredStartTimes.contains(selectedStart)) {
                    timePickerStart.setValue(selectedStart);
                }
            }
        });
        if(edit && master!=null){
            fio.setText(master.getFio());
            exp.setText(String.valueOf(master.getExperience()));
            hourRate.setText(master.getHourlyRate().toString());
            img.setImage(new Image("file:"+master.getAvatarPath()));
            imgPath.setText(master.getAvatarPath());
            String[] workingDays = master.getWorkingDay().split(",");
            for (String day : workingDays) {
                switch (day) {
                    case "Пн": Mn.setSelected(true); break;
                    case "Вт": Ts.setSelected(true); break;
                    case "Ср": Wd.setSelected(true); break;
                    case "Чт": Th.setSelected(true); break;
                    case "Пт": Fr.setSelected(true); break;} }
            timePickerStart.setValue(master.getStartTime().toString());
            timePickerEnd.setValue(master.getEndTime().toString());
        }

    }
    public void showWarningAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.show();
    }
}
