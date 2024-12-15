package com.example.salonclient.Controllers.Admin;

import com.example.salonclient.Model.BasicClasses.Service;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class ServicesController implements  LinkedToAdminMainWindow{
    @FXML private TableColumn<Service, String> name;
    @FXML private TableColumn<Service, String> description;
    @FXML private TableColumn<Service, BigDecimal> price;
    @FXML private TableColumn<Service, LocalTime> duration;
    @FXML public TableView<Service> ServicesTable;

    private  static  MainAdminViewController mainAdminViewController;
    @Override
    public void setMainAdminViewController(MainAdminViewController mainAdminViewController) {
        ServicesController.mainAdminViewController = mainAdminViewController;}

    private void showInfo(Service service){
        ServiceInfoOrEditController.setShow(true);
        ServiceInfoOrEditController.setEdit(false);
        ServiceInfoOrEditController.setCurService(service);
        ServiceInfoOrEditController.setServicesController(this);
        ServiceInfoOrEditController.setMainAdminViewController(mainAdminViewController);
        TransitionControl.style=true;
        TransitionControl.transition("serviceInfo.fxml", false, false);
    }
    @FXML private void toAddService(){
        ServiceInfoOrEditController.setServicesController(this);
        ServiceInfoOrEditController.setMainAdminViewController(mainAdminViewController);
        TransitionControl.style=true;
        TransitionControl.transition("addService.fxml", false, false);
    }
    public void refreshTable() {
        if (ServicesTable != null) {
            ServerAccess.sendListRequest("/admin/service", "GET", "", Service[].class,
                    response ->
                            Platform.runLater(() -> ServicesTable.setItems(FXCollections.observableArrayList(response))),
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных услуг: "
                                + error.getMessage(), ButtonType.OK);
                        alert.show();
                        return;
                    });
        }
    }
    public void refreshTable(List<Service> temporaryData) {
        if (ServicesTable != null) {
            ServicesTable.setItems(FXCollections.observableArrayList(temporaryData)); } }
    @FXML private void initialize(){
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        refreshTable();
        ServicesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) { showInfo(newValue); } });
    }
}
