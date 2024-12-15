package com.example.salonclient.Controllers.Client.Cards;

import com.example.salonclient.Controllers.Client.CatalogController;
import com.example.salonclient.Controllers.Client.CreateNoteController;
import com.example.salonclient.Controllers.Client.MainClientViewController;
import com.example.salonclient.Model.BasicClasses.Client;
import com.example.salonclient.Model.BasicClasses.Service;
import com.example.salonclient.Model.BasicClasses.User;
import com.example.salonclient.Model.TransitionControl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ServiceCardController {
    @FXML private Label duration;
    @FXML private Label description;
    @FXML private Label price;
    @FXML private Label name;
    @FXML private ImageView img;
    private Service service;
    private static MainClientViewController mainClientViewController;

    public void setMainClientViewController(MainClientViewController mainClientViewController) {
        ServiceCardController.mainClientViewController = mainClientViewController;}

    @FXML private void toCreateNote(){
        if(Client.getUser()!=null) {
            if(Client.getClient().getName().isEmpty()||Client.getClient().getTelephone().isEmpty()){
                showAlert("Введите имя и контактный номер телефона в данных аккаунта для записи!");
            }else {
                CreateNoteController.setService(service);
                CreateNoteController.setMainClientViewController(mainClientViewController);
                TransitionControl.style = true;
                TransitionControl.transition("createNote.fxml", false, false);
            }
        }
        else showAlert("Записаться на услугу может только авторизированный пользователь!");
    }
    public void setData(Service service){
        this.service = service;
        duration.setText("Продолжительность: "+ service.getDuration().getHour()+"ч "+service.getDuration().getMinute()+"мин");
        description.setText(service.getDescription());
        price.setText(service.getPrice()+" BYN");
        name.setText(service.getName());
        img.setImage(new Image("file:"+service.getImgPath()));
    }
    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING, message,ButtonType.OK);
        alert.showAndWait();
    }

}
