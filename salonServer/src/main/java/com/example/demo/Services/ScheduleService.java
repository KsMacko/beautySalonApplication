package com.example.demo.Services;

import com.example.demo.Entities.Client;
import com.example.demo.Entities.Master;
import com.example.demo.Entities.Schedule;
import com.example.demo.Repositories.ScheduleRepository;
import com.example.demo.Services.DTO.ScheduleDTO;
import com.example.demo.security.Exceptions.DataNotFound;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired ScheduleRepository scheduleRepository;
    @Autowired MasterService masterService;
    @Autowired ClientService clientService;

    @Transactional
    public void insertNote(Long masterLogin, String clientLogin, String date, String time){
        Master master = masterService.getMaster(masterLogin);
        Client client = clientService.getClient(clientLogin);
        Schedule schedule = new Schedule();
        schedule.setNoteTime(LocalTime.parse(time));
        schedule.setNoteDate(LocalDate.parse(date));
        schedule.setMaster(master);
        schedule.setClient(client);
        scheduleRepository.save(schedule);
    }
    @Transactional
    public void delete(Long id){
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(()->new DataNotFound("note not found"));
        scheduleRepository.delete(schedule);
    }
    public List<ScheduleDTO> getNotesByClient(String clientLogin){
        Client client = clientService.getClient(clientLogin);
        List<Schedule> schedules = scheduleRepository.findAllByClient(client).orElseThrow(()->
                new DataNotFound("notes not found by client"));
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: schedules){
            ScheduleDTO scheduleDTO = getDto(schedule);
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }
    public List<ScheduleDTO> getNotes(){
        List<Schedule> schedules = scheduleRepository.findAll();
        if(schedules.isEmpty()) throw new DataNotFound("notes not found by client");
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: schedules){
            ScheduleDTO scheduleDTO = getDto(schedule);
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }

    private static @NotNull ScheduleDTO getDto(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        Master master = schedule.getMaster();

        scheduleDTO.setScheduleId(schedule.getScheduleId());
        scheduleDTO.setServiceName(master.getService().getName());
        scheduleDTO.setDuration(master.getService().getDuration());
        scheduleDTO.setPrice(master.getService().getPrice());
        scheduleDTO.setFio(master.getFio());
        scheduleDTO.setTime(schedule.getNoteTime());
        scheduleDTO.setDate(schedule.getNoteDate());
        return scheduleDTO;
    }
    public List<ScheduleDTO> getNotesByDate(String date){
        List<Schedule> schedules = scheduleRepository.findAllByNoteDate(LocalDate.parse(date)).orElseThrow(()->
                new DataNotFound("notes not found by date"));
        List<ScheduleDTO> scheduleDTOS=new ArrayList<>();
        for(Schedule schedule: schedules){
            ScheduleDTO scheduleDTO = getDto(schedule);
            scheduleDTO.setClientName(schedule.getClient().getName());
            scheduleDTO.setTelephone(schedule.getClient().getTelephone());
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }

}
