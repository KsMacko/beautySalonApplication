package com.example.salonclient.Controllers.Client.Cards;

import com.example.salonclient.Controllers.Client.ChooseMasterController;
import com.example.salonclient.Model.BasicClasses.Master;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Setter;


public class MasterCardController {
    @Setter private static Boolean choose;
    @FXML private Label name;
    @FXML private Label experience;
    @FXML private ImageView img;

    private Master master;
    @FXML private Button select;


    @FXML private void toChoose(){
        ChooseMasterController.setChosenMaster(this.master);
        ChooseMasterController.resetSelection(ChooseMasterController.getMasterCardControllers());
        select.setStyle("-fx-background-color: rgba(183, 157, 117, 0.5);");
    }
    public void setData(Master master){
        this.master = master;
        name.setText(master.getFio());
        experience.setText("Опыт: "+String.valueOf(master.getExperience())+" лет");
        img.setImage(new Image("file:"+master.getAvatarPath()));
        if(choose=false){select.setDisable(true);}
    }

    public void deselect() { select.setStyle("-fx-background-color: transparent;"); }
}
