package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "master")
public class Master {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long masterLogin;
    private String fio;
    private int experience;
    private BigDecimal hourlyRate;
    private String avatarPath;

    @ManyToOne @JoinColumn(name = "master_schedule_id", nullable = false) private MasterSchedule master_schedule;

    @ManyToOne @JoinColumn(name = "service_id", nullable = false) private SalonService service;

    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY) private List<Schedule> schedule;
}
