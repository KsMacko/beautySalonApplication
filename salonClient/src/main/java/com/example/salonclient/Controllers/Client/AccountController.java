package com.example.salonclient.Controllers.Client;

import com.example.salonclient.Controllers.Admin.MainAdminViewController;
import com.example.salonclient.Controllers.Client.Cards.MasterCardController;
import com.example.salonclient.Controllers.Client.Cards.NoteCardController;
import com.example.salonclient.Model.BasicClasses.Client;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.BasicClasses.Schedule;
import com.example.salonclient.Model.GsonProvider;
import com.example.salonclient.Model.JsonConverting;
import com.example.salonclient.Model.ServerAccess;
import com.example.salonclient.Model.TransitionControl;
import com.example.salonclient.Model.BasicClasses.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@NoArgsConstructor
public class AccountController implements LinkedToMainWindow{
    public static boolean SignIn;
    @FXML private VBox account;
    @FXML private StackPane auth;
    @FXML private TextField login;
    @FXML private TextField password;
    @FXML private TextField repeatPassword;
    @FXML Label wayLabel;
    @FXML Button Enter;
    @FXML Button Change;

    @FXML private Label name;
    @FXML private Label telephone;
    @FXML private Label sex;
    @FXML private Label birthDate;
    @FXML private ImageView img;
    @FXML private FlowPane flow;

    private static MainClientViewController mainClientViewController;
    @Override
    public void setMainClientViewController(MainClientViewController controller) {
        mainClientViewController =controller;}

    @FXML public void toAuthorise(){
        if(ValidateData()){
            Map<String, Object> params = new HashMap<>();
            params.put("login", login.getText());
            params.put("password", password.getText());
            String url;
            if(SignIn) url = "/auth/toSignIn";
            else url = "/auth/toSignUp";
            String jsonInputString = JsonConverting.createJson(params);
            ServerAccess.sendRequest(url, "POST", jsonInputString,
                    response -> {
                        if (response.contains("Успех")) {
                            Platform.runLater(() -> {
                                User.setUser(new User(login.getText(), password.getText()));
                                Client.setClient(new Client());
                                if(response.contains("role")){
                                    handleRoleBasedRedirection(getRoleFromResponse(response));
                                }
                                else mainClientViewController.SetPage("account.fxml");
                            });
                        } else {
                            if(SignIn){
                                Alert alert = new Alert(Alert.AlertType.WARNING, "Пользователь не найден", ButtonType.OK);
                                alert.show();
                                return;
                            }
                            else{Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка при регистрации", ButtonType.OK);
                                alert.show();
                                return;}}
                    }, error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка при регистрации: "+error, ButtonType.OK);
                        alert.show();
                        return;});
        }
    }
    @FXML public void ChangeTheWay(){SignIn=!SignIn; initialize();}
    private boolean ValidateData(){
        if(((login.getText().isEmpty()||password.getText().isEmpty()||repeatPassword.getText().isEmpty())&&!SignIn) ||
                ((login.getText().isEmpty()||password.getText().isEmpty())&&SignIn)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Все поля должны быть заполнены", ButtonType.CANCEL);
            alert.show();
            return false;
        }
        if(!login.getText().matches("^[a-zA-Z0-9._-]+$")||!password.getText().matches("^[a-zA-Z0-9._-]+$")){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Логин и пароль могут содерржать только бквы латинского" +
                    " алфавита, цифры и спец. символы . _ -", ButtonType.CANCEL);
            alert.show();
            return false;
        }
        if (!SignIn && !password.getText().equals(repeatPassword.getText())){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Пароли не совпадают", ButtonType.CANCEL);
            alert.show();
            return false;
        }
        return true;
    }
    @FXML private void initialize(){
        if(User.getUser()==null){
            auth.setVisible(true);
            account.setVisible(false);
            if(SignIn){
                wayLabel.setText("Вход");
                Enter.setText("Войти");
                repeatPassword.setVisible(false);
                Change.setText("Регистрация");
            }else{
                wayLabel.setText("Регистрация");
                repeatPassword.setVisible(true);
                Enter.setText("Зарегистрироваться");
                Change.setText("Войти");
            }
        }else {
            auth.setVisible(false);
            account.setVisible(true);
            refreshData();
        }
    }
    @FXML private void toEditData(){
        EditPersonalDataController.setMainClientViewController(mainClientViewController);
        TransitionControl.style=true;
        TransitionControl.transition("editPersonalData.fxml", false, false);
    }
    @FXML private void toExit(){
        Client.setClient(null);
        User.setUser(null);
        TransitionControl.transition("mainClientView.fxml", true, true);
        mainClientViewController.SetPage("account.fxml");
    }
    private String getRoleFromResponse(String response) {
        try { JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("role");
        } catch (JSONException e) { e.printStackTrace(); return null; }
    }
    private void handleRoleBasedRedirection(String role) {
        if (role != null) {
            if (role.equals("ADMIN")) {
                MainAdminViewController.setMainClientViewController(mainClientViewController);
                TransitionControl.transition("mainAdminView.fxml", true, true);
            } else {
                ServerAccess.sendRequest("/client/account/" + User.getUser().getLogin(), "GET", "", Client.class,
                        response -> {
                            Client.setClient(response);
                            refreshData();
                            Platform.runLater(() -> mainClientViewController.SetPage("account.fxml"));
                        },
                        error -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Клиент не найден. Ошибка: " + error, ButtonType.CANCEL);
                            alert.show();
                            return;
                        });
            }
        } }

    public void setData(List<Schedule> schedules){
        flow.getChildren().clear();
        try {
            for(Schedule schedule:schedules) {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("/com/example/salonclient/clientNote.fxml"));
                Pane pane = load.load();
                NoteCardController cardC = load.getController();
                cardC.setAccountController(this);
                cardC.setData(schedule);
                flow.getChildren().add(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshData() {
        getClientInfo();
        assert Client.getClient() != null;
        ServerAccess.sendListRequest("/client/note?clientLogin="+Client.getClient().getLogin(), "GET", "",
                Schedule[].class,
                response ->
                        Platform.runLater(() -> {
                            System.out.println(response);
                            setData(response);}),
                error -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных услуг: "
                            + error.getMessage(), ButtonType.OK);
                    alert.show();
                    return;
                });

    }

    public void getClientInfo() {
        if (Client.getClient() != null) {
            System.out.println(Client.getClient().getName().isEmpty() + "  " + Client.getClient().getName());
            if (!Client.getClient().getName().isEmpty()) name.setText(Client.getClient().getName());
            if (!Client.getClient().getTelephone().isEmpty()) telephone.setText(Client.getClient().getTelephone());
            if (!Client.getClient().getSex().isEmpty()) {
                if (Client.getClient().getSex().equals("FEMALE")) sex.setText("Женский");
                else sex.setText("Мужской");
            }
            if (Client.getClient().getBirthDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String formattedDate = Client.getClient().getBirthDate().format(formatter);
                birthDate.setText(formattedDate);
            }
            try {
                if (!Client.getClient().getAvatarPath().isEmpty()) {
                    img.setImage(new Image("file:" + Client.getClient().getAvatarPath())); }
            } catch (Exception e) {
                img.setImage(new Image("file:resources/com/example/salonclient/defAvatar.jpg"));
            }
        }
    }
}
