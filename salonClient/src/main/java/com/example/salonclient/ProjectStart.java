package com.example.salonclient;

import com.example.salonclient.Controllers.Client.AccountController;
import com.example.salonclient.Controllers.Client.MainClientViewController;
import com.example.salonclient.Model.TransitionControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectStart extends Application {
    @Override
    public void start(Stage stage){
        AccountController.SignIn=true;
        TransitionControl.transition("mainClientView.fxml", true, false);
    }

    public static void main(String[] args) {
        launch();
    }
}