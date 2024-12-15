package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client {
    @Id private String clientLogin;
    private String name;
    private String telephone;
    private String avatarPath;
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING) private Sex sex;

    @OneToOne @MapsId private User user;
    @OneToMany(mappedBy = "client") private List<Schedule> schedule;
}
