package com.example.demo.Controllers;

import com.example.demo.Entities.*;
import com.example.demo.Services.ClientService;
import com.example.demo.Services.DTO.ClientDTO;
import com.example.demo.Services.DTO.MasterDTO;
import com.example.demo.Services.DTO.ScheduleDTO;
import com.example.demo.Services.DTO.ServiceDTO;
import com.example.demo.Services.MasterService;
import com.example.demo.Services.SalonServiceService;
import com.example.demo.Services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired private MasterService masterService;
    @Autowired private ClientService clientService;
    @Autowired private SalonServiceService salonService;
    @Autowired private ScheduleService scheduleService;

    @GetMapping("/master/{id}")
    public Master getMaster(@PathVariable Long id) {
            return masterService.getMaster(id);
    }
    @GetMapping("/master")
    public List<Master> getMasters() {
        return masterService.getMasters();
    }
    @PostMapping("/master")
    public String saveMaster(@RequestBody MasterDTO masterDTO) {
        masterService.save(masterDTO);
        return "success";
    }
    @DeleteMapping("/master")
    public String deleteMaster(@RequestBody Long id) {
        masterService.delete(id);
        return "success";
    }
    @GetMapping("/master/full")
    public List<MasterDTO> getFullMasterInfo(){
        return masterService.getFullMasterInfo();}
    @PutMapping("/master")
    public String updateMaster(@RequestBody MasterDTO masterDTO) {
        masterService.update(masterDTO);
        return "success";
    }
    @GetMapping("/client")
    public List<ClientDTO> getClients(){
        return clientService.getClients();
    }
    @GetMapping("/service")
    public List<ServiceDTO> getServices(){
        return salonService.getAllServices();
    }

    @GetMapping("/service/{id}")
    public ServiceDTO getService(@PathVariable Long id){
        return salonService.findDTOById(id);
    }
    @DeleteMapping("/service")
    public String deleteService(@RequestBody Long id){
        salonService.delete(id);
        return "success";
    }
    @PostMapping("/service")
    public String saveService(@RequestBody SalonService service){
        System.out.println(service.getName()+ service.getDescription()+ service.getServiceId()+ service.getImgPath()+ service.getPrice());
        salonService.save(service);
        System.out.println(service.getServiceId());
        return "success";
    }
    @PutMapping("/service")
    public String updateService(@RequestBody ServiceDTO serviceDTO) {
        salonService.update(serviceDTO);
        return "success";
    }
    @GetMapping("/note")
    public List<ScheduleDTO> getNotes(){
        return scheduleService.getNotes();
    }
    @GetMapping("/note/schedule")
    public List<ScheduleDTO> getNotesByDate(@RequestParam String date){
        return scheduleService.getNotesByDate(date);
    }
    @DeleteMapping("/note")
    public String delete(@RequestParam Long scheduleId){
        scheduleService.delete(scheduleId);
        return "success";
    }
    @PostMapping("/client/search_filter")
    public List<ClientDTO> getSearchedAndFilteredClients(@RequestBody Map<String, Object> criteria){
        String query = (String) criteria.get("searchQuery");
        criteria.remove("searchQuery");
        return clientService.searchAndFilterClients(query, criteria);
    }
    @PostMapping("/master/search_filter")
    public List<MasterDTO> getSearchedAndFilteredMasters(@RequestBody Map<String, Object> criteria){
        String query = (String) criteria.get("searchQuery");
        criteria.remove("searchQuery");
        return masterService.searchAndFilterMasters(query, criteria);
    }
}


