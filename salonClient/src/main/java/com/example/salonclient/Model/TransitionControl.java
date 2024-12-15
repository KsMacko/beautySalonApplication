package com.example.salonclient.Model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TransitionControl {
    public static Boolean style=false;
    static Stage stage;
    private static Map<String, Stage> stages = new HashMap<>();
    public static void transition(String filename,boolean fullscreen, boolean closeLast){
        try {
            if (stages.containsKey(filename)) {
                Stage existingStage = stages.get(filename);
                if (stage != null && closeLast) {
                    stage.close(); }
                existingStage.show();
                stage = existingStage;
                return; }
            FXMLLoader fxmlLoader = new FXMLLoader(TransitionControl.class.getResource("/com/example/salonclient/"+filename));
            Scene scene = new Scene(fxmlLoader.load());
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setOnHidden(event -> stages.remove(filename));
            if(fullscreen) newStage.setMaximized(true);
            if(style) newStage.initStyle(StageStyle.UNDECORATED);
            newStage.show();
            if (stage != null && closeLast) {
                stage.close(); }
            stages.put(filename,newStage);
            stage = newStage;
        }
        catch (IOException exception){
            System.out.println("Error "+ exception.getCause()+" occupied" );
            exception.printStackTrace();
        }
    }
    public static Stage getStage() {return stage;}
}

