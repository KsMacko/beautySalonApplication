package com.example.demo.Services.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ScheduleDTO {
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