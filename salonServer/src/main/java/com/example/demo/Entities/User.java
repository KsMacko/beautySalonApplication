package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id private String login;
    private String hashPassword;
    @Transient
    private String password;
    @Enumerated(EnumType.STRING) private Roles role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Client client;

}
