package com.example.demo.Services.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceDTO {
    private Long serviceId;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalTime duration;
    private String imgPath;
}
