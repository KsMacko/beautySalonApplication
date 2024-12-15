package com.example.demo.Services.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClientDTO {
    private String login;
    private String name;
    private String telephone;
    private String avatarPath;
    private LocalDate birthDate;
    private String sex;
}
