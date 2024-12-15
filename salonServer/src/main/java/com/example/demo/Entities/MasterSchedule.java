package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "master_schedule")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MasterSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long masterScheduleId;
    private String workingDay;
    private LocalTime startTime;
    private LocalTime endTime;

    @OneToMany(mappedBy = "master_schedule", fetch = FetchType.LAZY) private List<Master> masters;
}
