package com.example.salonclient.Model.BasicClasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    private Long serviceId;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalTime duration;
    private String imgPath;
    @Override public String toString(){
        return name;
    }
}
