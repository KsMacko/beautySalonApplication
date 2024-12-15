package com.example.demo.Controllers;

import com.example.demo.Services.*;
import com.example.demo.Services.DTO.ClientDTO;
import com.example.demo.Services.DTO.MasterDTO;
import com.example.demo.Services.DTO.ScheduleDTO;
import com.example.demo.Services.DTO.ServiceDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired ClientService clientService;
    @Autowired SalonServiceService salonService;
    @Autowired MasterService masterService;
    @Autowired BookingService bookingService;
    @Autowired ScheduleService scheduleService;

    @PostMapping
    public String saveClient(ClientDTO clientDTO){
        clientService.save(clientDTO);
        return "success";
    }
    @GetMapping("/service")
    public List<ServiceDTO> getServices(){
        return salonService.getAllServices();
    }
    @PatchMapping("/account/{login}")
    public String changeAvatar(@PathVariable String login, @NotNull String path){
        clientService.updateAvatar(login,path);
        return "success";
    }

    @PutMapping("/account")
    public String editPersonalData(@RequestBody ClientDTO client){
        clientService.update(client);
        return "success";
    }
    @DeleteMapping("/account/{login}")
    public String deleteAccount(@PathVariable String login){
        clientService.delete(login);
        return "success";
    }
    @GetMapping("/account/{login}")
    public ClientDTO getClient(@PathVariable String login){
        return clientService.getClientDTO(login);
    }

    @GetMapping("/master")
    public List<MasterDTO> getMasterByService(@RequestParam Long serviceId){
        return masterService.getMastersByService(serviceId);
    }
    @GetMapping("/master/all")
    public List<MasterDTO> getAllMasters(){
        return masterService.getAllMasters();
    }
    @GetMapping("/master/available")
    public boolean isMasterWorking(@RequestParam Long id, @RequestParam String date){
        LocalDate localDate = LocalDate.parse(date);
        return masterService.workingDay(localDate, id);
    }

    @GetMapping("/master/random")
    public MasterDTO getRandomMasterByService(@RequestParam Long serviceId){
        List<MasterDTO> masters = masterService.getMastersByService(serviceId);
        return masters.get(new Random().nextInt(masters.size()));
    }
    @GetMapping("/availableTimes")
    public List<LocalTime> getAvailableTimes(@RequestParam Long id, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return bookingService.getAvailableTimes( id, localDate);
    }
    @PostMapping("/note")
    public String save(@RequestParam Long id,
                       @RequestParam String clientLogin,
                       @RequestParam String date,
                       @RequestParam String time){
        scheduleService.insertNote(id, clientLogin, date, time);
        return "success";
    }
    @DeleteMapping("/note")
    public String save(@RequestParam Long scheduleId){
        scheduleService.delete(scheduleId);
        return "success";
    }
    @GetMapping("/note")
    public List<ScheduleDTO> getNotesByClient(@RequestParam String clientLogin){
        return scheduleService.getNotesByClient(clientLogin);
    }
    @PostMapping("/service/search_filter")
    public List<ServiceDTO> getSearchedAndFilteredServices(@RequestBody Map<String, Object> criteria){
        String query = (String) criteria.get("searchQuery");
        criteria.remove("searchQuery");
        return salonService.searchAndFilterServices(query, criteria);
    }

}
