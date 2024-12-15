package com.example.demo.Services.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MasterDTO{
    private Long id;
    private String fio;
    private String serviceName;
    private int experience;
    private BigDecimal hourlyRate;
    private String avatarPath;
    private String workingDay;
    private LocalTime startTime;
    private LocalTime endTime;
}
