package com.example.salonclient.Controllers.Admin;

import com.example.salonclient.Model.BasicClasses.Service;
import com.example.salonclient.Model.GsonProvider;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ServiceInfoOrEditController {
    private static Boolean show=false;
    private static Boolean edit=false;
    private static ServicesController servicesController;
    public static MainAdminViewController mainAdminViewController;

    public static void setShow(Boolean show) {ServiceInfoOrEditController.show = show;}
    public static void setEdit(Boolean edit) {ServiceInfoOrEditController.edit = edit;}
    public static void setServicesController(ServicesController servicesController) {
        ServiceInfoOrEditController.servicesController = servicesController;}
    public static void setMainAdminViewController(MainAdminViewController mainAdminViewController) {
        ServiceInfoOrEditController.mainAdminViewController = mainAdminViewController;}

    @FXML private Label name;
    @FXML private Label description;
    @FXML private Label price;
    @FXML private Label duration;
    @FXML private ImageView img;
    private static Service curService;
    public static void setCurService(Service curService) {ServiceInfoOrEditController.curService = curService;}
    @FXML private TextField nameInp;
    @FXML private TextField priceInp;
    @FXML private TextArea descriptionInp;
    @FXML private MenuButton durationH;
    @FXML private MenuButton durationM;
    @FXML private TextField imgPath;
    @FXML private void toClose(){

        if(edit){
            edit=false;
            servicesController.refreshTable();
            TransitionControl.transition("serviceInfo.fxml", false,true);
        }else{
            show=false;
            TransitionControl.style=false;
            TransitionControl.transition("mainAdminView.fxml", true,true);
            mainAdminViewController.SetPage("services.fxml");
        }

    }
    @FXML public void toFindTheImage(){
        FindImage(imgPath, img);
    }
    @FXML private void toSave(){
        Service service = createService();
        if(service!=null) {
            String jsonInputString = GsonProvider.createGson().toJson(service);
            if(edit){
                ServerAccess.sendRequest("/admin/service", "PUT", jsonInputString,
                        response -> {
                            Platform.runLater(() -> {
                                if(response.contains("success")){
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Услуга сохранена!", ButtonType.OK);
                                    alert.showAndWait();
                                    edit = false;
                                    show = true;
                                    setCurService(service);
                                    servicesController.refreshTable();
                                    TransitionControl.transition("serviceInfo.fxml", false, true);}
                            });
                        },
                        error -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка при сохранении изменений: " + error, ButtonType.OK);
                            alert.show();
                        });
            }else {
                ServerAccess.sendRequest("/admin/service", "POST", jsonInputString,
                        response -> {
                            Platform.runLater(() -> {
                                if (response.contains("success")) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Услуга добавлена!", ButtonType.OK);
                                    alert.show();
                                    servicesController.refreshTable();
                                    TransitionControl.style = false;
                                    show = false;
                                    TransitionControl.transition("mainAdminView.fxml", true, true);
                                }
                                mainAdminViewController.SetPage("services.fxml");
                            });
                        },
                        error -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка сохранения данных: " + error, ButtonType.OK);
                            alert.show();
                        });
            }
        }
    }
   @FXML private void toDelete(){
       if (curService != null) {
           Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить услугу?", ButtonType.YES, ButtonType.CANCEL);
           Optional<ButtonType> result = alert.showAndWait();
           if (result.isPresent() && result.get() == ButtonType.YES) {
               ServerAccess.sendRequest("/admin/service", "DELETE", curService.getServiceId().toString(),
                       response ->
                               Platform.runLater(() -> {
                                   if (response.contains("success")) {
                                       Alert infAlert = new Alert(Alert.AlertType.INFORMATION, "Услуга удалена!", ButtonType.OK);
                                       infAlert.showAndWait();
                                       servicesController.refreshTable();
                                       TransitionControl.style=false;
                                       show = false;
                                       TransitionControl.transition("mainAdminView.fxml", true,true);
                                       mainAdminViewController.SetPage("services.fxml");
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
    @FXML private void toOpenUpdate(){
        show=false;
        TransitionControl.style=true;
        edit=true;
        TransitionControl.transition("addService.fxml", false, true);
    }

    @FXML private void initialize(){
        if(show){
            name.setText(curService.getName());
            description.setText(curService.getDescription());
            price.setText(curService.getPrice().toString());
            duration.setText(curService.getDuration().format(DateTimeFormatter.ofPattern("HH:mm")));
            img.setImage(new Image("file:"+ curService.getImgPath()));
        }
        else {
            if (edit) {
                nameInp.setText(curService.getName());
                descriptionInp.setText(curService.getDescription());
                priceInp.setText(curService.getPrice().toString());
                LocalTime duration = curService.getDuration();
                durationH.setText(String.valueOf(duration.getHour()));
                durationM.setText(String.valueOf(duration.getMinute()));
                img.setImage(new Image("file:" + curService.getImgPath()));
                imgPath.setText(curService.getImgPath());
            }

            durationH.getItems().clear();
            durationM.getItems().clear();
            for (int hour = 0; hour <= 7; hour++) {
                MenuItem menuItem = new MenuItem(String.valueOf(hour));
                menuItem.setOnAction(event -> durationH.setText(menuItem.getText()));
                durationH.getItems().add(menuItem);
            }
            for (int min = 0; min <= 59; min++) {
                MenuItem menuItem = new MenuItem(String.valueOf(min));
                menuItem.setOnAction(event -> durationM.setText(menuItem.getText()));
                durationM.getItems().add(menuItem);
            }
        }
    }
    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.CANCEL);
        alert.show();}
    private Service createService(){
        if(nameInp.getText().isEmpty()||descriptionInp.getText().isEmpty()||priceInp.getText().isEmpty()||
                durationH.getText().isEmpty()||durationM.getText().isEmpty()||imgPath.getText().isEmpty()){
            showAlert("Все поля должны быть заполнены!");
            return null;}
        BigDecimal priceVal;
        try { priceVal = new BigDecimal(priceInp.getText()).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            try { priceVal = new BigDecimal(Integer.parseInt(priceInp.getText())).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e2) {
                showAlert("Цена должна быть числом с плавающей запятой до 2 знаков или целым числом!");
                return null; }
        }
        if (priceVal.compareTo(BigDecimal.ZERO) < 0) {
            showAlert("Цена не может быть отрицательным числом!");
            return null;
        }
        Service service = new Service();
        if(edit){service.setServiceId(curService.getServiceId());}
        else service.setServiceId(0L);

        service.setName(nameInp.getText());
        service.setDescription(descriptionInp.getText());
        service.setDuration(LocalTime.of(
                Integer.parseInt(durationH.getText()),
                Integer.parseInt(durationM.getText())));
        service.setImgPath(imgPath.getText());
        service.setPrice(priceVal);
        return service;
    }
    public static void FindImage(TextField imgPath, ImageView img) {
        FileChooser fileChooser = new FileChooser();
        List<String> formats = Arrays.asList("*.png", "*.jpg", "*.webp", "*.jpeg");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", formats));
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            imgPath.setText(file.getAbsolutePath());
            img.setImage(new Image("file:"+ imgPath.getText()));
        }
    }
}
