package com.example.demo.Services;

import com.example.demo.Entities.SalonService;
import com.example.demo.Repositories.SalonServiceRepository;
import com.example.demo.Services.DTO.ServiceDTO;
import com.example.demo.security.Exceptions.DataNotFound;
import com.example.demo.security.Exceptions.DeleteDataException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SalonServiceService {
    @Autowired SalonServiceRepository salonServiceRepository;
    @Autowired GenericSearchService<SalonService> genericSearchService;

    public List<ServiceDTO> getAllServices(){
        List<SalonService> services = salonServiceRepository.findAll();
        return convertSalonService(services);}

    public ServiceDTO findDTOById(Long id){
        SalonService service = salonServiceRepository.findByServiceId(id)
                .orElseThrow(() -> new DataNotFound("Service not found!"));
        return new ServiceDTO(service.getServiceId(), service.getName(), service.getDescription(),
                service.getPrice(), service.getDuration(), service.getImgPath());}
    private List<ServiceDTO> convertSalonService(List<SalonService> services){
        return services.stream()
                .map(service -> new ServiceDTO(service.getServiceId(), service.getName(), service.getDescription(),
                        service.getPrice(), service.getDuration(), service.getImgPath()))
                .collect(Collectors.toList());
    }
    public SalonService findById(Long id){
        return salonServiceRepository.findById(id).orElseThrow(()->
                new DataNotFound("service not found!"));
    }
    public SalonService findByName(String name){
        return salonServiceRepository.findByName(name).orElseThrow(() ->
                new DataNotFound("Service not found!"));}
    @Transactional
    public void save(SalonService serviceDTO){
//        salonServiceRepository.save(service);
        SalonService service = new SalonService();
        service.setServiceId(serviceDTO.getServiceId());
        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        service.setPrice(serviceDTO.getPrice());
        service.setDuration(serviceDTO.getDuration());
        service.setImgPath(serviceDTO.getImgPath());
        salonServiceRepository.save(service);
    }
    @Transactional
    public void update(ServiceDTO serviceDTO) {
        SalonService service = salonServiceRepository.findByServiceId(serviceDTO.getServiceId()).orElseThrow(() ->
                new DataNotFound("Service not found"));
        if(!serviceDTO.getName().isEmpty()) service.setName(serviceDTO.getName());
        if(!serviceDTO.getDescription().isEmpty()) service.setDescription(serviceDTO.getDescription());
        if(serviceDTO.getPrice().compareTo(BigDecimal.ZERO) > 0) service.setPrice(serviceDTO.getPrice());
        if(serviceDTO.getDuration()!=null) service.setDuration(serviceDTO.getDuration());
        if(!serviceDTO.getImgPath().isEmpty()) service.setImgPath(serviceDTO.getImgPath());
        salonServiceRepository.save(service);
//        if(Objects.equals(service, newService)) throw new UpdateDataException("Error while updating service!");
//        else salonServiceRepository.save(newService);
    }
    @Transactional
    public void delete(Long id){
        SalonService service = salonServiceRepository.findByServiceId(id).orElseThrow(() ->
                new DataNotFound("Service not found!"));
        salonServiceRepository.delete(service);
        if(salonServiceRepository.findByServiceId(service.getServiceId()).isPresent()){
            throw new DeleteDataException("Error while deleting service");
        }}
    public List<ServiceDTO> searchAndFilterServices(String query, Map<String, Object> criteria) {
        List<ServiceDTO> services = getAllServices();
        if(query!=null&&!query.isEmpty())
            services = convertSalonService(genericSearchService.search(SalonService.class, query));
        if(criteria!=null&&!criteria.isEmpty()) services = GenericFilterUtility.filter(services, criteria);
        return services;
    }
}
