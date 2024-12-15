package com.example.salonclient.Model.BasicClasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Long scheduleId;
    private String clientName;
    private String telephone;
    private String fio;
    private String serviceName;
    private BigDecimal price;
    private LocalTime duration;
    private LocalDate date;
    private LocalTime time;
}
