package com.example.salonclient.Model.BasicClasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Getter private static User user;
    @Getter @Setter private String login;
    @Getter @Setter private String password;
    public static void setUser(User user) {User.user = user;}

}
