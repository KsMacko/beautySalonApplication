package com.example.salonclient.Controllers.Client;

import com.example.salonclient.Controllers.Admin.ServiceInfoOrEditController;
import com.example.salonclient.Model.BasicClasses.Client;
import com.example.salonclient.Model.GsonProvider;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
import javafx.util.Callback;
import lombok.Setter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditPersonalDataController {
    @FXML private TextField name;
    @FXML private TextField telephone;
    @FXML private TextField birthDate;
    @FXML private TextField imgPath;
    @FXML private ImageView img;
    @FXML private MenuButton sex;
    @FXML private MenuItem male;
    @FXML private MenuItem female;

    private Popup popup;

    private LocalDate selectedDate;
    @Setter private static MainClientViewController mainClientViewController;
    private boolean isPopupVisible = false;

    @FXML private void toClose(){
        TransitionControl.style=false;
        TransitionControl.transition("mainClientView.fxml", true, true);
        mainClientViewController.SetPage("account.fxml");
    }
    @FXML private void toSave(){
        if(name.getText().isEmpty()||telephone.getText().isEmpty()||imgPath.getText().isEmpty()||birthDate.getText().isEmpty()){
            showAlert("Все поля должны быть заполнены!");
            return;
        }
        else {
            Client client = new Client();
            client.setLogin(Client.getClient().getLogin());
            if (!name.getText().isEmpty()) {
                if (name.getText().matches("^[а-яА-ЯёЁ]+$")) {
                    client.setName(name.getText());
                } else {
                    showAlert("Имя может содержать только кириллицу!");
                    return;
                }
            }
            if (!telephone.getText().isEmpty()) {
                if (telephone.getText().matches("^(\\+?375)(33|44|29|25)\\d{7}$")) {
                    client.setTelephone(telephone.getText());
                    if (!telephone.getText().startsWith("+")) {
                        client.setTelephone("+" + telephone.getText());
                    }
                } else {
                    showAlert("Неверный формат ввода номера!");
                    return;
                }
            }
            if (!sex.getText().isEmpty()) {
                if (sex.getText().equals("Женский")) client.setSex("FEMALE");
                else client.setSex("MALE");
            }
            if (!imgPath.getText().isEmpty()) client.setAvatarPath(imgPath.getText());
            if (selectedDate != null) client.setBirthDate(selectedDate);
            String jsonInputString = GsonProvider.createGson().toJson(client);
            ServerAccess.sendRequest("/client/account", "PUT", jsonInputString,
                    response -> {
                        if (response.contains("success")) {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Данные обновлены!", ButtonType.OK);
                                alert.showAndWait();
                                Client.setClient(client);
                                TransitionControl.style = false;
                                TransitionControl.transition("mainClientView.fxml", true, true);
                                mainClientViewController.SetPage("account.fxml");
                            });
                        }
                    },
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка обновления данных аккаунта: "
                                + error.getMessage(), ButtonType.OK);
                        alert.show();
                        return;
                    });
        }
    }
    @FXML private void FindImage(){
        ServiceInfoOrEditController.FindImage(imgPath, img);
    }
    @FXML private void setCalendar() {
        if (isPopupVisible) {
            popup.hide();
            isPopupVisible = false;
            return; }
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {

            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
                if (empty || item == null || item.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-text-fill: #7b7878;");
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setStyle("-fx-background-color: transparent;");
                    if (item.equals(selectedDate))
                        setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-background-color: #c0a57b;");
                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);

        datePicker.setOnAction(e -> {
            selectedDate = datePicker.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            birthDate.setText(selectedDate.format(formatter));
             popup.hide();
        });

        DatePickerSkin datePickerSkin = new DatePickerSkin(datePicker);
        Node popupContent = datePickerSkin.getPopupContent();

        if (popup != null) { popup.hide(); }
        popup = new Popup();
        popup.getContent().add(popupContent);
        Bounds boundsInScene = birthDate.localToScreen(birthDate.getBoundsInLocal());
        popup.setX(boundsInScene.getMinX());
        popup.setY(boundsInScene.getMinY() + birthDate.getHeight());
        popup.show(birthDate.getScene().getWindow());
        isPopupVisible = true;
    }

    @FXML private void initialize(){
        if(Client.getClient()!=null){
            if (!Client.getClient().getName().isEmpty()) name.setText(Client.getClient().getName());
            if (!Client.getClient().getTelephone().isEmpty()) telephone.setText(Client.getClient().getTelephone());
            if (!Client.getClient().getSex().isEmpty()) {
                if (Client.getClient().getSex().equals("FEMALE")) sex.setText("Женский");
                else sex.setText("Мужской");
            }
            male.setOnAction(e -> sex.setText("Мужской"));
            female.setOnAction(e -> sex.setText("Женский"));
            if (Client.getClient().getBirthDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String formattedDate = Client.getClient().getBirthDate().format(formatter);
                birthDate.setText(formattedDate);
                selectedDate=Client.getClient().getBirthDate();}
            if(!Client.getClient().getAvatarPath().isEmpty()){
                imgPath.setText(Client.getClient().getAvatarPath());}
            if (!Client.getClient().getAvatarPath().isEmpty()) {
                img.setImage(new Image("file:" + Client.getClient().getAvatarPath())); }
        }
    }
    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING,message, ButtonType.OK);
        alert.showAndWait();
    }
}
