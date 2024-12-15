package com.example.salonclient.Controllers.Admin;

import com.example.salonclient.Controllers.Admin.Cards.AddOrEditMasterController;
import com.example.salonclient.Controllers.Admin.Cards.MasterCardController;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.GsonProvider;
import com.example.salonclient.Model.JsonConverting;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

public class MastersController implements  LinkedToAdminMainWindow{
    @FXML private VBox vBox;
    @FXML private TableColumn<Master, String> fio;
    @FXML private TableColumn<Master, String> serviceName;
    @FXML private TableColumn<Master, Integer> experience;
    @FXML private TableColumn<Master, BigDecimal> hourlyRate;
    @FXML private TableColumn<Master, String> workingDay;
    @FXML private TableColumn<Master, LocalTime> workingTime;
    @FXML public TableView<Master> MastersTable;

    private Map<String, Object> criteria = new HashMap<>();
    private String searchInp;

    @FXML private TextField search;
    @FXML private CheckBox Mn;
    @FXML private CheckBox Ts;
    @FXML private CheckBox Wd;
    @FXML private CheckBox Th;
    @FXML private CheckBox Fr;

    @FXML private TextField expMinValue;
    @FXML private TextField hourRateMinValue;
    @FXML private TextField expMaxValue;
    @FXML private TextField hourRateMaxValue;

    @FXML private ComboBox<String> timePickerStart;
    @FXML private ComboBox<String> timePickerEnd;

    private  static  MainAdminViewController mainAdminViewController;

    public void setMainAdminViewController(MainAdminViewController mainAdminViewController) {
        MastersController.mainAdminViewController = mainAdminViewController;}

    @FXML private void toAddMaster(){
        AddOrEditMasterController.setMastersController(this);
        AddOrEditMasterController.setMainAdminViewController(mainAdminViewController);
        TransitionControl.style=true;
        TransitionControl.transition("addMaster.fxml", false, false);
    }
    public void SetMaster(Master master, String path){
        vBox.getChildren().clear();
        try {
            FXMLLoader load = new FXMLLoader();
            load.setLocation(getClass().getResource("/com/example/salonclient/"+path));
            VBox pane = load.load();
            if(path.equals("masterCard.fxml")) {
                MasterCardController cardC = load.getController();
                cardC.setData(master);
                cardC.setMastersController(this);
            }
            VBox.setVgrow(pane, Priority.ALWAYS);
            vBox.getChildren().add(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        SummaryMainController.FillMastersTable(MastersTable);
    }
    @FXML private void toUpdate(){
        expMinValue.setText("");
        expMaxValue.setText("");
        hourRateMinValue.setText("");
        hourRateMaxValue.setText("");
        timePickerStart.setValue("");
        timePickerEnd.setValue("");
        search.setText("");
        List<CheckBox> days = Arrays.asList(Mn, Ts, Wd, Th, Fr);
        for (CheckBox day : days) {
            if (day.isSelected()) {
                day.setSelected(false);}}
        criteria.clear();
        refreshTable();
    }
    @FXML private void initialize() {
        fio.setCellValueFactory(new PropertyValueFactory<>("fio"));
        serviceName.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        experience.setCellValueFactory(new PropertyValueFactory<>("experience"));
        hourlyRate.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        workingDay.setCellValueFactory(new PropertyValueFactory<>("workingDay"));
        workingTime.setCellValueFactory(new PropertyValueFactory<>("workingTime"));
        refreshTable();
        MastersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SetMaster(newValue, "masterCard.fxml");
            }
        });
        setWorkingTimeValues(timePickerStart, timePickerEnd);
        expMinValue.textProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        expMaxValue.textProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        hourRateMinValue.textProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        hourRateMaxValue.textProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());

        timePickerStart.valueProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        timePickerEnd.valueProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());

        Mn.selectedProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        Ts.selectedProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        Wd.selectedProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        Th.selectedProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        Fr.selectedProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            searchInp = newValue;
            updateCriteriaAndRefreshTable();
        });

    }

    public static void setWorkingTimeValues(ComboBox<String> timePickerStart, ComboBox<String> timePickerEnd) {
        List<String> startTimes = IntStream.rangeClosed(10, 19)
                .mapToObj(hour -> String.format("%02d:00", hour))
                .toList();
        List<String> endTimes = IntStream.rangeClosed(11, 20)
                .mapToObj(hour -> String.format("%02d:00", hour))
                .toList();
        timePickerStart.getItems().addAll(startTimes);
        timePickerEnd.getItems().addAll(endTimes);

        timePickerStart.setOnAction(event -> {
            String selectedStart = timePickerStart.getValue();
            if(selectedStart!=null) {
                int startHour = Integer.parseInt(selectedStart.split(":")[0]);
                List<String> filteredEndTimes = IntStream.rangeClosed(startHour + 1, 20)
                        .mapToObj(hour -> String.format("%02d:00", hour))
                        .toList();
                String selectedEnd = timePickerEnd.getValue();
                timePickerEnd.getItems().setAll(filteredEndTimes);

                if (filteredEndTimes.contains(selectedEnd)) {
                    timePickerEnd.setValue(selectedEnd);
                }
            }
        });
    }

    private Boolean Validate(){
        return checkForInteger(expMinValue)
                && checkForInteger(expMaxValue)
                && validateAndUpdateBigDecimalInput(hourRateMinValue)
                && validateAndUpdateBigDecimalInput(hourRateMaxValue);
    }

    private Boolean checkForInteger(TextField expMaxValue) {
        if(!expMaxValue.getText().isEmpty()){
            try{
                int experience = Integer.parseInt(expMaxValue.getText());
                if (experience < 0) {
                    showAlert("Опыт работы не может быть отрицательным числом!");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Опыт работы должен быть целым числом!");
                return false;}
        }
        return true;

    }

    private void updateCriteriaAndRefreshTable() {
        if(Validate()) {
            criteria.clear();
            if (!expMinValue.getText().isEmpty()) {
                criteria.put("experienceMin", Integer.valueOf(expMinValue.getText()));
            }
            if (!expMaxValue.getText().isEmpty()) {
                criteria.put("experienceMax", Integer.valueOf(expMaxValue.getText()));
            }
            if (!hourRateMinValue.getText().isEmpty()&&!hourRateMinValue.getText().endsWith(".")) {
                try {
                    BigDecimal hourlyRateMax = new BigDecimal(hourRateMinValue.getText())
                            .setScale(2, RoundingMode.HALF_UP);
                    criteria.put("hourlyRateMin", hourlyRateMax.toString());
                } catch (NumberFormatException e) {
                    BigDecimal hourlyRateMax = new BigDecimal(Integer.parseInt(hourRateMinValue.getText()))
                            .setScale(2, RoundingMode.HALF_UP);
                    criteria.put("hourlyRateMin", hourlyRateMax.toString()); }
            }
            if (!hourRateMaxValue.getText().isEmpty()&&!hourRateMaxValue.getText().endsWith(".")) {
                try {
                    BigDecimal hourlyRateMax = new BigDecimal(hourRateMaxValue.getText())
                            .setScale(2, RoundingMode.HALF_UP);
                    criteria.put("hourlyRateMix", hourlyRateMax.toString());
                } catch (NumberFormatException e) {
                    BigDecimal hourlyRateMax = new BigDecimal(Integer.parseInt(hourRateMaxValue.getText()))
                            .setScale(2, RoundingMode.HALF_UP);
                    criteria.put("hourlyRateMax", hourlyRateMax.toString()); }
            }
            if (timePickerStart.getValue() != null&&!timePickerStart.getValue().isEmpty()) {
                criteria.put("startTimeMin", LocalTime.parse(timePickerStart.getValue()));
            }
            if (timePickerEnd.getValue() != null&&!timePickerEnd.getValue().isEmpty()) {
                criteria.put("endTimeMax", LocalTime.parse(timePickerEnd.getValue()));
            }
            if (!getSelectedDays().isEmpty()) {
                List<String> workingDays = new ArrayList<>();
                if (Mn.isSelected()) workingDays.add("Пн");
                if (Ts.isSelected()) workingDays.add("Вт");
                if (Wd.isSelected()) workingDays.add("Ср");
                if (Th.isSelected()) workingDays.add("Чт");
                if (Fr.isSelected()) workingDays.add("Пт");
                criteria.put("workingDay", String.join(",", workingDays));
            }
            if (searchInp != null && !searchInp.isEmpty()) { criteria.put("searchQuery", searchInp); }
            String jsonInputString = JsonConverting.createJson(criteria);
            ServerAccess.sendListRequest("/admin/master/search_filter", "POST",
                    jsonInputString, Master[].class,
                    response -> Platform.runLater(() -> MastersTable.setItems(FXCollections.observableArrayList(response))),
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных мастеров: " + error.getMessage(), ButtonType.OK);
                        alert.show();
                    });
        }
    }
    public ArrayList<String> getSelectedDays() {
        List<CheckBox> days = Arrays.asList(Mn, Ts, Wd, Th, Fr);
        ArrayList<String> selectedDays = new ArrayList<>();
        for (CheckBox day : days) {
            if (day.isSelected()) {
                selectedDays.add(day.getText());}}
        return selectedDays;
    }
    private Boolean validateAndUpdateBigDecimalInput(TextField textField) {
        String text = textField.getText();
        if (!text.matches("^-?\\d*\\.?\\d{0,2}$")) {
            if (text.replaceAll("[^.]","").length() > 1) {
                showAlert("Вы не можете вводить более одной точки!");
            } else if (text.matches("^-?\\d*\\.\\d{3,}$")) {
                showAlert("Вы не можете вводить более двух цифр после запятой!");
            } else { showAlert("Часовая ставка должна быть числом с плавающей запятой до 2 знаков или целым числом!"); }
        }else {
            return true;}
        return false;
    }

        public void showAlert(String message){
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            alert.show();
        }

}
