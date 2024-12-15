package com.example.salonclient.Model.BasicClasses;

import com.example.salonclient.Controllers.Admin.SummaryMainController;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class Master{
    private Long id;
    private String fio;
    private String serviceName;
    private int experience;
    private BigDecimal hourlyRate;
    private String avatarPath;
    private String workingDay;
    private LocalTime startTime;
    private LocalTime endTime;

    public Master(){}

    public String getWorkingTime() {
        return  startTime.toString()+" - "+endTime.toString();
    }
    public Integer getWorkingHours(){
        if (SummaryMainController.getChosenYear() == null ||
                SummaryMainController.getChosenMonth() == null) { return 0;}
        YearMonth yearMonth = YearMonth.of(SummaryMainController.getChosenYear(),
                Month.valueOf( SummaryMainController.getChosenMonth()).getValue());
        LocalDate endDate = yearMonth.atEndOfMonth();
        if (YearMonth.now().equals(yearMonth)) {
            endDate = LocalDate.now(); }
        int totalHours = 0;
        List<String> workingDays = Arrays.asList(workingDay.toLowerCase().split(","));
        for (LocalDate date = yearMonth.atDay(1); !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, new Locale("ru"));
            if (workingDays.contains(dayName)) {
                totalHours += endTime.getHour() - startTime.getHour();
                totalHours += (endTime.getMinute() - startTime.getMinute()) / 60.0; }
        }
        return totalHours;
    }
    public BigDecimal getSalary(){
        int workingHours = getWorkingHours();
        return hourlyRate.multiply(BigDecimal.valueOf(workingHours));
    }
}
