package com.example.demo.Services;

import com.example.demo.Entities.Master;
import com.example.demo.Entities.MasterSchedule;
import com.example.demo.Entities.SalonService;
import com.example.demo.Entities.Schedule;
import com.example.demo.Repositories.ScheduleRepository;
import com.example.demo.security.Exceptions.DataNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired private MasterService masterService;
    @Autowired private ScheduleRepository scheduleRepository;

    public List<LocalTime> getAvailableTimes( Long login, LocalDate date) {
        List<LocalTime> availableTimes = new ArrayList<>();
        Master master = masterService.getMaster(login);
        MasterSchedule schedule = master.getMaster_schedule();
        if(!masterService.workingDay(date,login)){
            return availableTimes;
        }
        List<Schedule> bookings = scheduleRepository.findAllByMasterAndNoteDate(master, date).orElseThrow(() ->
                new DataNotFound("Schedule data not found!"));
        SalonService service = master.getService();
        long serviceDurationMinutes = service.getDuration().getHour() * 60 + service.getDuration().getMinute();
        Duration serviceDuration = Duration.ofMinutes(serviceDurationMinutes);

        // Создаем список времени в интервалах по 30 минут
        LocalTime currentTime = schedule.getStartTime();
        while ((currentTime.plus(serviceDuration).isBefore(schedule.getEndTime())
                || currentTime.plus(serviceDuration).equals(schedule.getEndTime()))) {
            final LocalTime slotTime = currentTime;
            boolean isFree = bookings.stream().noneMatch(b ->
                    slotTime.isBefore(b.getNoteTime().plus(serviceDuration)) &&
                            b.getNoteTime().isBefore(slotTime.plus(serviceDuration)) );

            if (isFree) { availableTimes.add(currentTime); }
            currentTime = currentTime.plusMinutes(30);
        }

        return availableTimes;
    }
}

