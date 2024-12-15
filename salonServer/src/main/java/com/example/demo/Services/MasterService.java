package com.example.demo.Services;

import com.example.demo.Entities.*;
import com.example.demo.Repositories.MasterRepository;
import com.example.demo.Repositories.MasterScheduleRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.DTO.MasterDTO;
import com.example.demo.security.Exceptions.DataNotFound;
import com.example.demo.security.Exceptions.DeleteDataException;
import com.example.demo.security.Exceptions.SaveDataException;
import com.example.demo.security.Exceptions.UpdateDataException;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MasterService {
    @Autowired private MasterRepository masterRepository;
    @Autowired private SalonServiceService salonService;
    @Autowired private MasterScheduleRepository masterScheduleRepository;
    @Autowired private GenericSearchService<Master> genericSearchService;

    public Master getMaster(Long login) {
        return masterRepository.findByMasterLogin(login).orElseThrow(() ->
                new DataNotFound("Master not found"));
    }
    public List<Master> getMasters() {
        List<Master> masters = masterRepository.findAll();
//        if (masters.isEmpty()) { throw new DataNotFound("Masters not found"); }
        return masters;

    }
    public boolean workingDay(LocalDate date, Long login){
        MasterSchedule schedule = getMaster(login).getMaster_schedule();
        return isWorkingDay(schedule.getWorkingDay(), date.getDayOfWeek());
    }
    public List<MasterDTO> getMastersByService(Long serviceId) {
        List<Master> masters = masterRepository.findAllByService(salonService.findById(serviceId)).orElseThrow(() ->
                new DataNotFound("Master not found"));
        return getMasterDTOS(masters);
    }
    public List<MasterDTO> getAllMasters() {
        List<Master> masters = masterRepository.findAll();
        return getMasterDTOS(masters);
    }

    @NotNull
    private List<MasterDTO> getMasterDTOS(List<Master> masters) {
        List<MasterDTO> masterDTOS = new ArrayList<>();
        for(Master master: masters){
            MasterDTO masterDTO = new MasterDTO();
            masterDTO.setFio(master.getFio());
            masterDTO.setAvatarPath(master.getAvatarPath());
            masterDTO.setExperience(master.getExperience());
            masterDTO.setId(master.getMasterLogin());
            MasterSchedule masterSchedule = master.getMaster_schedule();
            masterDTO.setWorkingDay(masterSchedule.getWorkingDay());
            masterDTO.setStartTime(masterSchedule.getStartTime());
            masterDTO.setEndTime(masterSchedule.getEndTime());
            masterDTOS.add(masterDTO);
        }
        return masterDTOS;
    }
    private List<MasterDTO> getFullMasterDTOS(List<Master> masters) {
        List<MasterDTO> masterDTOS = new ArrayList<>();
        for(Master master: masters){
            MasterDTO masterDTO = new MasterDTO();
            masterDTO.setFio(master.getFio());
            masterDTO.setAvatarPath(master.getAvatarPath());
            masterDTO.setExperience(master.getExperience());
            masterDTO.setHourlyRate(master.getHourlyRate());
            masterDTO.setId(master.getMasterLogin());
            masterDTO.setServiceName(master.getService().getName());
            MasterSchedule masterSchedule = master.getMaster_schedule();
            masterDTO.setWorkingDay(masterSchedule.getWorkingDay());
            masterDTO.setStartTime(masterSchedule.getStartTime());
            masterDTO.setEndTime(masterSchedule.getEndTime());
            masterDTOS.add(masterDTO);
        }
        return masterDTOS;
    }

    public List<MasterDTO> getFullMasterInfo(){
        List<MasterDTO> masterDTOS = new ArrayList<>();
        List<Master> masters = getMasters();
        for(Master master: masters){
            MasterDTO masterDTO = new MasterDTO();
            masterDTO.setFio(master.getFio());
            masterDTO.setAvatarPath(master.getAvatarPath());
            masterDTO.setExperience(master.getExperience());
            masterDTO.setHourlyRate(master.getHourlyRate());

            SalonService salonService = master.getService();
            masterDTO.setServiceName(salonService.getName());
            masterDTO.setId(master.getMasterLogin());
            MasterSchedule masterSchedule = master.getMaster_schedule();
            masterDTO.setWorkingDay(masterSchedule.getWorkingDay());
            masterDTO.setStartTime(masterSchedule.getStartTime());
            masterDTO.setEndTime(masterSchedule.getEndTime());
            masterDTOS.add(masterDTO);
        }
        return masterDTOS;
    }
    @Transactional
    public void update(MasterDTO masterDTO) {
        Master master = masterRepository.findByMasterLogin(masterDTO.getId()).orElseThrow(() ->
                new DataNotFound("Master not found"));
        if (masterDTO.getFio() != null && !masterDTO.getFio().isEmpty()) {
            master.setFio(masterDTO.getFio());
        }
        if (masterDTO.getExperience() > 0 && masterDTO.getExperience() < 70) {
            master.setExperience(masterDTO.getExperience());
        }
        if (masterDTO.getHourlyRate().compareTo(BigDecimal.ZERO) > 0) {
            master.setHourlyRate(masterDTO.getHourlyRate());
        }
        if (masterDTO.getAvatarPath() != null && !masterDTO.getAvatarPath().isEmpty()) {
            master.setAvatarPath(masterDTO.getAvatarPath());
        }
        if (masterDTO.getServiceName() != null && !masterDTO.getServiceName().isEmpty()) {
            SalonService service = salonService.findByName(masterDTO.getServiceName());
            master.setService(service);
        }
        MasterSchedule schedule = master.getMaster_schedule();
        if (masterDTO.getWorkingDay() != null && !masterDTO.getWorkingDay().isEmpty()) {
            schedule.setWorkingDay(masterDTO.getWorkingDay());
        }
        if (masterDTO.getStartTime() != null) {
            schedule.setStartTime(masterDTO.getStartTime());
        }
        if (masterDTO.getEndTime() != null) {
            schedule.setEndTime(masterDTO.getEndTime());
        }

        masterScheduleRepository.save(schedule);
        master.setMaster_schedule(schedule);
        masterRepository.save(master);

        if (masterRepository.findByMasterLogin(masterDTO.getId()).isEmpty()) {
            throw new UpdateDataException("Ошибка обновления данных мастера!");
        }
    }

    @Transactional
    public void delete(Long login) {
        Master master = masterRepository.findByMasterLogin(login).orElseThrow(() ->
                new DataNotFound("Master not found"));
        masterRepository.delete(master);
//        if(masterRepository.findByMasterLogin(login).isPresent()){
//            throw new DeleteDataException("Ошибка удаления данных!");}
    }
    @Transactional
    public void save(MasterDTO masterDTO){
        Master master = new Master();
        master.setMasterLogin(masterDTO.getId());
        master.setFio(masterDTO.getFio());
        master.setExperience(masterDTO.getExperience());
        master.setHourlyRate(masterDTO.getHourlyRate());
        master.setAvatarPath(masterDTO.getAvatarPath());
        SalonService service = salonService.findByName(masterDTO.getServiceName());
        master.setService(service);

        MasterSchedule schedule = new MasterSchedule();
        schedule.setWorkingDay(masterDTO.getWorkingDay());
        schedule.setStartTime(masterDTO.getStartTime());
        schedule.setEndTime(masterDTO.getEndTime());
        masterScheduleRepository.save(schedule);
        master.setMaster_schedule(schedule);
        masterRepository.save(master);
    if(masterRepository.findByMasterLogin(master.getMasterLogin()).isEmpty())
        throw new SaveDataException("Ошибка при сохранении данных  мастера!");
    }

    private boolean isWorkingDay(String workingDays, DayOfWeek dayOfWeek) {
        Map<DayOfWeek, String> dayOfWeekMap = new HashMap<>();
        dayOfWeekMap.put(DayOfWeek.MONDAY, "Пн");
        dayOfWeekMap.put(DayOfWeek.TUESDAY, "Вт");
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, "Ср");
        dayOfWeekMap.put(DayOfWeek.THURSDAY, "Чт");
        dayOfWeekMap.put(DayOfWeek.FRIDAY, "Пт");
        dayOfWeekMap.put(DayOfWeek.SATURDAY, "Сб");
        dayOfWeekMap.put(DayOfWeek.SUNDAY, "Вс");

        String[] days = workingDays.split(",");
        String dayName = dayOfWeekMap.get(dayOfWeek);
        for (String day : days) {
            if (day.equalsIgnoreCase(dayName)) { return true; } }
        return false;
    }

    public List<MasterDTO> searchAndFilterMasters(String query, Map<String, Object> criteria) {
        List<MasterDTO> masters = getFullMasterInfo();
        if(query!=null&&!query.isEmpty()) masters = getFullMasterDTOS(genericSearchService.search(Master.class, query));
        if(criteria!=null&&!criteria.isEmpty()) masters = GenericFilterUtility.filter(masters, criteria);
        return masters;
    }
}
