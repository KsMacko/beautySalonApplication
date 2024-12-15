package com.example.demo.Services;

import com.example.demo.Entities.Client;
import com.example.demo.Entities.MasterSchedule;
import com.example.demo.Repositories.MasterScheduleRepository;
import com.example.demo.Services.DTO.ClientDTO;
import com.example.demo.Services.DTO.MasterDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MasterScheduleService {
    @Autowired MasterScheduleRepository masterScheduleRepository;

    public List<MasterSchedule> getMasterSchedule(){
        return masterScheduleRepository.findAll();}
    public MasterSchedule findById(Long id){return masterScheduleRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("Master schedule not found"));}
    @Transactional
    public void save(MasterSchedule masterSchedule){masterScheduleRepository.save(masterSchedule);}
    @Transactional
    public void updateMasterSchedule(Long id, LocalTime startTime, LocalTime endTime, String workingDay) {
        MasterSchedule masterSchedule = masterScheduleRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Master schedule not found"));
        masterSchedule.setStartTime(startTime);
        masterSchedule.setEndTime(endTime);
        masterSchedule.setWorkingDay(workingDay);
        masterScheduleRepository.save(masterSchedule); }
    @Transactional
    public void delete(MasterSchedule masterSchedule){masterScheduleRepository.delete(masterSchedule);}

}
