package com.example.salonclient.Model.BasicClasses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Client extends User {
    @Getter @Setter private static Client client;
    private String name="";
    private String telephone="";
    private String avatarPath="C:\\Users\\Солнышко\\Documents\\GitHub\\salonClient\\src\\main\\resources\\com\\example\\salonclient\\defAvatar.jpg";
    private LocalDate birthDate=null;
    private String sex="FEMALE";
    public Client(){
        super(User.getUser().getLogin(), User.getUser().getPassword());}
    public Client(String name,String telephone, String avatarPath,LocalDate date, String sex){
        super(User.getUser().getLogin(), User.getUser().getPassword());
        this.name=name;
        this.telephone=telephone;
        this.avatarPath=avatarPath;
        this.sex=sex;
        this.birthDate=date;}
}
