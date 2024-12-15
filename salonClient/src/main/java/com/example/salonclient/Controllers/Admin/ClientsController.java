package com.example.salonclient.Controllers.Admin;

import com.example.salonclient.Model.BasicClasses.Client;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.JsonConverting;
import com.example.salonclient.Model.ServerAccess;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClientsController {
    @FXML private TableColumn<Client, String> name;
    @FXML private TableColumn<Client, String> telephone;
    @FXML private TableColumn<Client, String> sex;
    @FXML private TableColumn<Client, LocalDate> birthDate;
    @FXML public TableView<Client> ClientsTable;

    @FXML private MenuButton sexChoice;
    @FXML private MenuItem male;
    @FXML private MenuItem female;
    @FXML private ComboBox<String> minYear;
    @FXML private ComboBox<String> maxYear;
    @FXML private TextField search;
    private Map<String, Object> criteria = new HashMap<>();
    private String searchInp;

    private void updateCriteriaAndRefreshTable() {
            criteria.clear();
            if (!sexChoice.getText().isEmpty()) {
                if (sexChoice.getText().equals("Женский"))
                    criteria.put("sex", "FEMALE");
                else criteria.put("sex","MALE");
            }
            if (minYear.getValue()!=null) {
                int year = Integer.parseInt(minYear.getValue());
                LocalDate date = LocalDate.of(year, 1, 1);
                criteria.put("birthDateMin", date);
            }
            if (maxYear.getValue()!=null) {
                int year = Integer.parseInt(maxYear.getValue());
                LocalDate date = LocalDate.of(year, 12, 31);
                criteria.put("birthDateMax", date);
            }
            if (searchInp != null && !searchInp.isEmpty()) { criteria.put("searchQuery", searchInp); }
            String jsonInputString = JsonConverting.createJson(criteria);
            ServerAccess.sendListRequest("/admin/client/search_filter", "POST",
                    jsonInputString, Client[].class,
                    response -> Platform.runLater(() -> ClientsTable.setItems(FXCollections.observableArrayList(response))),
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных мастеров: " + error.getMessage(), ButtonType.OK);
                        alert.show();
                    });
    }
    @FXML private void initialize(){
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        telephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        sex.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.equals("FEMALE") ? "Женский" : "Мужской"); } }
        });
        sex.setCellValueFactory(new PropertyValueFactory<>("sex"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthDate.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null);
                } else { setText(item.format(formatter)); } } });
        birthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        male.setOnAction(e -> {
            sexChoice.setText("Мужской");
            updateCriteriaAndRefreshTable(); });
        female.setOnAction(e -> {
            sexChoice.setText("Женский");
            updateCriteriaAndRefreshTable(); });
        for (int year = LocalDate.now().getYear() - 90; year <= LocalDate.now().getYear(); year++) {
            minYear.getItems().add(String.valueOf(year));
            maxYear.getItems().add(String.valueOf(year));
        }
        minYear.valueProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        maxYear.valueProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            searchInp = newValue;
            updateCriteriaAndRefreshTable(); });
        refreshData();
    }
    public void refreshData(){
        ServerAccess.sendListRequest("/admin/client", "GET", "", Client[].class,
                response ->
                        Platform.runLater(() ->{
                            if (ClientsTable!=null)
                                ClientsTable.setItems(FXCollections.observableArrayList(response));
                        }),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных масетров: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });
    }
    @FXML private void toUpdate(){
        minYear.setValue(null);
        maxYear.setValue(null);
        search.setText("");
        criteria.clear();
        refreshData();
    }
}
